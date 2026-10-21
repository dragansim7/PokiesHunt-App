package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class SiteRepository(
  private val context: Context,
  private val dao: BonusSiteDao,
  private val scannerEngine: DomainScannerEngine = DomainScannerEngine()
) {

  private val _isScanning = MutableStateFlow(false)
  val isScanning = _isScanning.asStateFlow()

  private val _scanningDomain = MutableStateFlow<String?>(null)
  val scanningDomain = _scanningDomain.asStateFlow()

  private val shouldStopScan = AtomicBoolean(false)

  val statsFlow: Flow<SiteStats> = combine(
    dao.getTotalCountFlow(),
    dao.getBonusCountFlow(),
    dao.getNoBonusCountFlow(),
    dao.getUncheckedCountFlow()
  ) { total, bonus, noBonus, unchecked ->
    SiteStats(total = total, bonus = bonus, noBonus = noBonus, unchecked = unchecked)
  }

  val rankedBonusSitesFlow: Flow<List<BonusSiteEntity>> = dao.getRankedBonusSitesFlow()
  val unrankedSitesFlow: Flow<List<BonusSiteEntity>> = dao.getUnrankedSitesFlow()
  val allSitesFlow: Flow<List<BonusSiteEntity>> = dao.getAllSitesFlow()

  suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
    val count = dao.getCount()
    if (count == 0) {
      Log.i("SiteRepository", "Pre-populating database from seed_sites.json...")
      loadSeedSitesFromAssets()
    }
  }

  private suspend fun loadSeedSitesFromAssets() = withContext(Dispatchers.IO) {
    try {
      context.assets.open("seed_sites.json").use { inputStream ->
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.readText()
        val jsonArray = JSONArray(jsonString)

        val sites = mutableListOf<BonusSiteEntity>()
        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          sites.add(
            BonusSiteEntity(
              id = obj.optString("id", "site_${i + 1}"),
              name = obj.optString("name", "Site ${i + 1}"),
              domain = obj.optString("domain", ""),
              category = obj.optString("category").takeIf { it.isNotBlank() },
              referral_url = obj.optString("referral_url", ""),
              referral_code = obj.optString("referral_code").takeIf { it.isNotBlank() },
              max_withdrawal = obj.optDouble("max_withdrawal", 0.0),
              deposit_required = obj.optBoolean("deposit_required", false),
              turnover = obj.optString("turnover", "—"),
              status = obj.optString("status", "unchecked"),
              scan_note = obj.optString("scan_note").takeIf { it.isNotBlank() },
              last_checked = if (obj.has("last_checked") && !obj.isNull("last_checked")) obj.optLong("last_checked") else null
            )
          )
        }
        dao.insertAll(sites)
        Log.i("SiteRepository", "Successfully inserted ${sites.size} seed sites.")
      }
    } catch (e: Exception) {
      Log.e("SiteRepository", "Failed to load seed sites: ${e.message}", e)
    }
  }

  suspend fun rescanSingleSite(site: BonusSiteEntity): ExtractionResult {
    _scanningDomain.value = site.domain
    val result = scannerEngine.scanSite(site)
    dao.updateScanResult(
      id = site.id,
      status = result.status,
      maxWithdrawal = result.maxWithdrawal,
      depositRequired = result.depositRequired,
      turnover = result.turnover,
      scanNote = result.scanNote,
      lastChecked = System.currentTimeMillis(),
      responseTimeMs = result.responseTimeMs,
      httpStatus = result.httpStatus
    )
    _scanningDomain.value = null
    return result
  }

  suspend fun startBatchScan(scope: CoroutineScope, limit: Int = 100) = withContext(Dispatchers.IO) {
    if (_isScanning.value) return@withContext
    _isScanning.value = true
    shouldStopScan.set(false)

    try {
      val unchecked = dao.getUncheckedSites(limit)
      for (site in unchecked) {
        if (shouldStopScan.get()) break
        _scanningDomain.value = site.domain
        val result = scannerEngine.scanSite(site)
        dao.updateScanResult(
          id = site.id,
          status = result.status,
          maxWithdrawal = result.maxWithdrawal,
          depositRequired = result.depositRequired,
          turnover = result.turnover,
          scanNote = result.scanNote,
          lastChecked = System.currentTimeMillis(),
          responseTimeMs = result.responseTimeMs,
          httpStatus = result.httpStatus
        )
      }
    } finally {
      _isScanning.value = false
      _scanningDomain.value = null
    }
  }

  fun pauseScanning() {
    shouldStopScan.set(true)
    _isScanning.value = false
    _scanningDomain.value = null
  }

  suspend fun saveSite(site: BonusSiteEntity) = withContext(Dispatchers.IO) {
    dao.insert(site)
  }

  suspend fun toggleFavorite(siteId: String) = withContext(Dispatchers.IO) {
    dao.toggleFavorite(siteId)
  }

  suspend fun exportDataAsCsv(): String = withContext(Dispatchers.IO) {
    val sites = dao.getAllSitesList()
    val sb = StringBuilder()
    sb.append("Rank,Name,Domain,Max Withdrawal,Deposit Required,Turnover,Status,Scan Note,Referral URL\n")
    val bonusSites = sites.filter { it.status == "bonus" }.sortedByDescending { it.max_withdrawal }
    val otherSites = sites.filter { it.status != "bonus" }

    var rank = 1
    for (s in bonusSites) {
      sb.append("${rank++},\"${s.name}\",\"${s.domain}\",${s.max_withdrawal},${s.deposit_required},\"${s.turnover}\",\"${s.status}\",\"${s.scan_note.orEmpty()}\",\"${s.referral_url}\"\n")
    }
    for (s in otherSites) {
      sb.append("—,\"${s.name}\",\"${s.domain}\",${s.max_withdrawal},${s.deposit_required},\"${s.turnover}\",\"${s.status}\",\"${s.scan_note.orEmpty()}\",\"${s.referral_url}\"\n")
    }
    sb.toString()
  }

  suspend fun syncWithBase44Cloud(): Int = withContext(Dispatchers.IO) {
    try {
      val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

      val request = Request.Builder()
        .url("https://app.base44.com/api/apps/6ad91b9ecf0fe8479b23719a/entities/BonusSite?limit=1000")
        .header("User-Agent", "Mozilla/5.0")
        .build()

      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return@withContext 0
        val bodyStr = response.body?.string() ?: return@withContext 0
        val jsonArray = JSONArray(bodyStr)
        var updatedCount = 0

        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          val domain = obj.optString("domain", "").lowercase().trim()
          if (domain.isBlank()) continue

          val existing = dao.getSiteByDomain(domain)
          val status = obj.optString("status", "unchecked")
          val maxW = obj.optDouble("max_withdrawal", 0.0)
          val depReq = obj.optBoolean("deposit_required", false)
          val turnover = obj.optString("turnover", "—")
          val note = obj.optString("scan_note").takeIf { it.isNotBlank() }

          if (existing != null) {
            if (status == "bonus" || existing.status == "unchecked") {
              dao.updateScanResult(
                id = existing.id,
                status = status,
                maxWithdrawal = maxW,
                depositRequired = depReq,
                turnover = turnover,
                scanNote = note ?: existing.scan_note,
                lastChecked = System.currentTimeMillis(),
                responseTimeMs = existing.response_time_ms,
                httpStatus = existing.http_status
              )
              updatedCount++
            }
          } else {
            dao.insert(
              BonusSiteEntity(
                id = obj.optString("id", "cloud_$i"),
                name = obj.optString("name", domain.split(".")[0]),
                domain = domain,
                category = obj.optString("category").takeIf { it.isNotBlank() },
                referral_url = obj.optString("referral_url", "https://$domain"),
                max_withdrawal = maxW,
                deposit_required = depReq,
                turnover = turnover,
                status = status,
                scan_note = note
              )
            )
            updatedCount++
          }
        }
        updatedCount
      }
    } catch (e: Exception) {
      Log.e("SiteRepository", "Sync with Base44 failed: ${e.message}")
      0
    }
  }
}
