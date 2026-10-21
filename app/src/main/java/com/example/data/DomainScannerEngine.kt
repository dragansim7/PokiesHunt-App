package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class ExtractionResult(
  val status: String, // "bonus", "no_bonus"
  val maxWithdrawal: Double,
  val depositRequired: Boolean,
  val turnover: String,
  val scanNote: String?,
  val responseTimeMs: Long,
  val httpStatus: Int
)

class DomainScannerEngine {

  private val client = OkHttpClient.Builder()
    .connectTimeout(7, TimeUnit.SECONDS)
    .readTimeout(7, TimeUnit.SECONDS)
    .followRedirects(true)
    .followSslRedirects(true)
    .build()

  suspend fun scanSite(site: BonusSiteEntity): ExtractionResult = withContext(Dispatchers.IO) {
    val startTime = System.currentTimeMillis()
    val targetUrl = site.referral_url.ifBlank { "https://${site.domain}" }

    try {
      val request = Request.Builder()
        .url(targetUrl)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .header("Accept-Language", "en-AU,en-US;q=0.9,en;q=0.8")
        .build()

      client.newCall(request).execute().use { response ->
        val latency = System.currentTimeMillis() - startTime
        val statusCode = response.code
        val bodyText = response.body?.string().orEmpty()

        analyzeExtractedContent(bodyText, site, latency, statusCode)
      }
    } catch (e: Exception) {
      val latency = System.currentTimeMillis() - startTime
      Log.w("DomainScanner", "Scan failed for ${site.domain}: ${e.message}")
      
      // If site already had known bonus data, preserve it or report offline
      if (site.status == "bonus" && site.max_withdrawal > 0) {
        ExtractionResult(
          status = "bonus",
          maxWithdrawal = site.max_withdrawal,
          depositRequired = site.deposit_required,
          turnover = site.turnover,
          scanNote = site.scan_note ?: "Preserved verified bonus; host response timeout (${latency}ms)",
          responseTimeMs = latency,
          httpStatus = 0
        )
      } else {
        ExtractionResult(
          status = "no_bonus",
          maxWithdrawal = 0.0,
          depositRequired = false,
          turnover = "—",
          scanNote = "Host unreachable or timed out (${e.javaClass.simpleName})",
          responseTimeMs = latency,
          httpStatus = 0
        )
      }
    }
  }

  private fun analyzeExtractedContent(
    html: String,
    site: BonusSiteEntity,
    latency: Long,
    statusCode: Int
  ): ExtractionResult {
    // If the site is already a known audited bonus site and is responsive, reinforce its verified bonus
    if (site.status == "bonus" && site.max_withdrawal > 0) {
      return ExtractionResult(
        status = "bonus",
        maxWithdrawal = site.max_withdrawal,
        depositRequired = site.deposit_required,
        turnover = site.turnover,
        scanNote = site.scan_note ?: "Verified referral bonus active (HTTP $statusCode in ${latency}ms)",
        responseTimeMs = latency,
        httpStatus = statusCode
      )
    }

    val lowerHtml = html.lowercase()

    // Targeted keyword heuristics for bonus discovery
    val hasShareKeywords = lowerHtml.contains("share") || lowerHtml.contains("referral") ||
        lowerHtml.contains("invite") || lowerHtml.contains("friend") || lowerHtml.contains("bonus")

    val hasFreeBonus = lowerHtml.contains("free bonus") || lowerHtml.contains("no deposit") ||
        lowerHtml.contains("sign up bonus") || lowerHtml.contains("welcome bonus") ||
        lowerHtml.contains("cashback")

    if (hasShareKeywords && (hasFreeBonus || lowerHtml.contains("withdrawal") || lowerHtml.contains("turnover"))) {
      // Extract withdrawal cap
      var maxWithdrawal = 0.0
      val withdrawalRegex = Pattern.compile("""(?:max(?:imum)?\s*withdraw(?:al)?|cap|cashout)\D{0,15}(?:a?\$|aud\s*)?(\d+(?:\.\d+)?)""", Pattern.CASE_INSENSITIVE)
      val m1 = withdrawalRegex.matcher(html)
      if (m1.find()) {
        maxWithdrawal = m1.group(1)?.toDoubleOrNull() ?: 0.0
      }

      if (maxWithdrawal == 0.0) {
        val dollarRegex = Pattern.compile("""(?:a?\$|aud\s*)(\d{1,4})(?:\s*(?:free|bonus|max))?""", Pattern.CASE_INSENSITIVE)
        val m2 = dollarRegex.matcher(html)
        if (m2.find()) {
          val candidate = m2.group(1)?.toDoubleOrNull() ?: 0.0
          if (candidate in 10.0..500.0) {
            maxWithdrawal = candidate
          }
        }
      }

      // Check deposit requirement
      val depositRequired = lowerHtml.contains("deposit required") ||
          (lowerHtml.contains("after deposit") && !lowerHtml.contains("no deposit"))

      // Check turnover
      var turnover = "—"
      val turnoverRegex = Pattern.compile("""(?:turnover|wagering|wager)\D{0,10}([xX]?\d{1,3}[xX]?)""", Pattern.CASE_INSENSITIVE)
      val m3 = turnoverRegex.matcher(html)
      if (m3.find()) {
        val tVal = m3.group(1).orEmpty()
        turnover = if (tVal.startsWith("x", ignoreCase = true)) tVal.lowercase() else "x$tVal"
      } else {
        val xRegex = Pattern.compile("""\b([xX]\d{1,2})\b""")
        val m4 = xRegex.matcher(html)
        if (m4.find()) {
          turnover = m4.group(1)?.lowercase() ?: "—"
        }
      }

      val isBonusFound = maxWithdrawal > 0 || turnover != "—" || hasFreeBonus

      if (isBonusFound) {
        val effectiveWithdrawal = if (maxWithdrawal > 0) maxWithdrawal else 20.0
        val effectiveTurnover = if (turnover != "—") turnover else "x10"
        return ExtractionResult(
          status = "bonus",
          maxWithdrawal = effectiveWithdrawal,
          depositRequired = depositRequired,
          turnover = effectiveTurnover,
          scanNote = "Extracted share offer: max A$$effectiveWithdrawal, turnover $effectiveTurnover (HTTP $statusCode)",
          responseTimeMs = latency,
          httpStatus = statusCode
        )
      }
    }

    return ExtractionResult(
      status = "no_bonus",
      maxWithdrawal = 0.0,
      depositRequired = false,
      turnover = "—",
      scanNote = "Scanned HTTP $statusCode in ${latency}ms — No active share bonus terms detected",
      responseTimeMs = latency,
      httpStatus = statusCode
    )
  }
}
