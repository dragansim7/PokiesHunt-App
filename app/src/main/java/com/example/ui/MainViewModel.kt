package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BonusSiteEntity
import com.example.data.SiteRepository
import com.example.data.SiteStats
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String, val shortLabel: String) {
  WITHDRAWAL("Highest max withdrawal", "max withdrawal"),
  TURNOVER("Lowest turnover", "lowest turnover"),
  VALUE("Best value — bonus, then turnover", "best value"),
  NO_DEPOSIT("No deposit needed first", "no deposit first"),
  RECENT("Recently checked", "recently checked"),
  NAME("Name A–Z", "name")
}

enum class FilterOption(val label: String) {
  ALL("All bonus sites"),
  FREE("No deposit needed"),
  DEPOSIT("Deposit required")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  val repository = SiteRepository(application, database.bonusSiteDao())

  val stats = repository.statsFlow.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    SiteStats()
  )

  val isScanning = repository.isScanning
  val scanningDomain = repository.scanningDomain

  val sortOption = MutableStateFlow(SortOption.WITHDRAWAL)
  val filterOption = MutableStateFlow(FilterOption.ALL)
  val searchQuery = MutableStateFlow("")

  val editingSite = MutableStateFlow<BonusSiteEntity?>(null)
  val viewingSite = MutableStateFlow<BonusSiteEntity?>(null)
  val showAddEditDialog = MutableStateFlow(false)
  val isUnrankedExpanded = MutableStateFlow(false)

  private val _uiEvents = MutableSharedFlow<String>()
  val uiEvents = _uiEvents.asSharedFlow()

  // Filtered and sorted ranked sites
  val rankedSites = combine(
    repository.rankedBonusSitesFlow,
    sortOption,
    filterOption,
    searchQuery
  ) { sites, sort, filter, query ->
    val q = query.trim().lowercase()

    val filtered = sites.filter { site ->
      val matchesQuery = q.isEmpty() ||
          site.name.lowercase().contains(q) ||
          site.domain.lowercase().contains(q) ||
          site.category?.lowercase()?.contains(q) == true

      val matchesDeposit = when (filter) {
        FilterOption.ALL -> true
        FilterOption.FREE -> !site.deposit_required
        FilterOption.DEPOSIT -> site.deposit_required
      }

      matchesQuery && matchesDeposit
    }

    filtered.sortedWith { a, b ->
      when (sort) {
        SortOption.WITHDRAWAL -> {
          val diff = b.max_withdrawal.compareTo(a.max_withdrawal)
          if (diff != 0) diff else parseTurnover(a.turnover).compareTo(parseTurnover(b.turnover))
        }
        SortOption.TURNOVER -> {
          val diff = parseTurnover(a.turnover).compareTo(parseTurnover(b.turnover))
          if (diff != 0) diff else b.max_withdrawal.compareTo(a.max_withdrawal)
        }
        SortOption.VALUE -> {
          val depDiff = (if (a.deposit_required) 1 else 0).compareTo(if (b.deposit_required) 1 else 0)
          if (depDiff != 0) depDiff else {
            val tDiff = parseTurnover(a.turnover).compareTo(parseTurnover(b.turnover))
            if (tDiff != 0) tDiff else b.max_withdrawal.compareTo(a.max_withdrawal)
          }
        }
        SortOption.NO_DEPOSIT -> {
          val depDiff = (if (a.deposit_required) 1 else 0).compareTo(if (b.deposit_required) 1 else 0)
          if (depDiff != 0) depDiff else b.max_withdrawal.compareTo(a.max_withdrawal)
        }
        SortOption.RECENT -> {
          val aTime = a.last_checked ?: 0L
          val bTime = b.last_checked ?: 0L
          bTime.compareTo(aTime)
        }
        SortOption.NAME -> a.name.compareTo(b.name, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Unranked sites (no bonus or unchecked)
  val unrankedSites = combine(
    repository.unrankedSitesFlow,
    searchQuery
  ) { sites, query ->
    val q = query.trim().lowercase()
    if (q.isEmpty()) sites
    else sites.filter {
      it.name.lowercase().contains(q) ||
          it.domain.lowercase().contains(q) ||
          it.category?.lowercase()?.contains(q) == true
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      repository.initializeIfNeeded()
    }
  }

  private fun parseTurnover(turnover: String?): Double {
    if (turnover.isNullOrBlank() || turnover == "—") return 999.0
    val numStr = turnover.replace("[^0-9.]".toRegex(), "")
    return numStr.toDoubleOrNull() ?: 999.0
  }

  fun setSort(option: SortOption) {
    sortOption.value = option
  }

  fun setFilter(option: FilterOption) {
    filterOption.value = option
  }

  fun setSearch(query: String) {
    searchQuery.value = query
  }

  fun toggleUnrankedExpanded() {
    isUnrankedExpanded.value = !isUnrankedExpanded.value
  }

  fun openAddSiteDialog() {
    editingSite.value = null
    showAddEditDialog.value = true
  }

  fun openEditSiteDialog(site: BonusSiteEntity) {
    editingSite.value = site
    showAddEditDialog.value = true
  }

  fun openSiteDetail(site: BonusSiteEntity) {
    viewingSite.value = site
  }

  fun closeDialogs() {
    showAddEditDialog.value = false
    editingSite.value = null
    viewingSite.value = null
  }

  fun saveSite(
    name: String,
    domain: String,
    category: String?,
    referralUrl: String,
    maxWithdrawal: Double,
    depositRequired: Boolean,
    turnover: String,
    status: String,
    scanNote: String?
  ) {
    viewModelScope.launch {
      val existing = editingSite.value
      val cleanDomain = domain.trim().lowercase().removePrefix("https://").removePrefix("http://").removePrefix("www.")
      val cleanRefUrl = if (referralUrl.isBlank()) "https://$cleanDomain" else referralUrl.trim()

      val site = BonusSiteEntity(
        id = existing?.id ?: "custom_${System.currentTimeMillis()}",
        name = name.trim().ifEmpty { cleanDomain.split(".")[0] },
        domain = cleanDomain,
        category = category?.trim()?.takeIf { it.isNotEmpty() },
        referral_url = cleanRefUrl,
        max_withdrawal = maxWithdrawal,
        deposit_required = depositRequired,
        turnover = turnover.trim().ifEmpty { "—" },
        status = status,
        scan_note = scanNote?.trim(),
        last_checked = System.currentTimeMillis()
      )
      repository.saveSite(site)
      closeDialogs()
      _uiEvents.emit("Saved ${site.name}")
    }
  }

  fun rescanSite(site: BonusSiteEntity) {
    viewModelScope.launch {
      _uiEvents.emit("Scanning ${site.domain}...")
      val result = repository.rescanSingleSite(site)
      if (result.status == "bonus") {
        _uiEvents.emit("Bonus verified on ${site.name}: A$${result.maxWithdrawal.toInt()} max withdrawal!")
      } else {
        _uiEvents.emit("Scanned ${site.domain}: no bonus offer found")
      }
    }
  }

  fun startScanAll() {
    viewModelScope.launch {
      repository.startBatchScan(viewModelScope, limit = 200)
    }
  }

  fun pauseScan() {
    repository.pauseScanning()
    viewModelScope.launch {
      _uiEvents.emit("Scanner paused")
    }
  }

  fun syncWithCloud() {
    viewModelScope.launch {
      _uiEvents.emit("Syncing with cloud index...")
      val count = repository.syncWithBase44Cloud()
      _uiEvents.emit("Synced $count updates from cloud")
    }
  }

  fun toggleFavorite(siteId: String) {
    viewModelScope.launch {
      repository.toggleFavorite(siteId)
    }
  }
}
