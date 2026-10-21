package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddEditSiteDialog
import com.example.ui.components.BonusSiteCard
import com.example.ui.components.ExportDialog
import com.example.ui.components.FilterSortRow
import com.example.ui.components.HeaderSection
import com.example.ui.components.ScanProgressCard
import com.example.ui.components.SearchBar
import com.example.ui.components.SiteDetailSheet
import com.example.ui.components.StatsGrid
import com.example.ui.components.UnrankedSection
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber950
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InstrumentSerifFamily
import com.example.ui.theme.MutedForeground
import com.example.ui.theme.MutedForegroundSubtle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val stats by viewModel.stats.collectAsState()
  val isScanning by viewModel.isScanning.collectAsState()
  val scanningDomain by viewModel.scanningDomain.collectAsState()
  val currentSort by viewModel.sortOption.collectAsState()
  val currentFilter by viewModel.filterOption.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val rankedSites by viewModel.rankedSites.collectAsState()
  val unrankedSites by viewModel.unrankedSites.collectAsState()
  val isUnrankedExpanded by viewModel.isUnrankedExpanded.collectAsState()
  val editingSite by viewModel.editingSite.collectAsState()
  val viewingSite by viewModel.viewingSite.collectAsState()
  val showAddEditDialog by viewModel.showAddEditDialog.collectAsState()

  var showExportDialog by remember { mutableStateOf(false) }
  var exportCsvContent by remember { mutableStateOf("") }

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val showScrollToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 4 }
  }

  LaunchedEffect(Unit) {
    viewModel.uiEvents.collectLatest { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = DarkBackground,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End,
        modifier = Modifier.padding(bottom = 16.dp)
      ) {
        // Export button
        FloatingActionButton(
          onClick = {
            coroutineScope.launch {
              exportCsvContent = viewModel.repository.exportDataAsCsv()
              showExportDialog = true
            }
          },
          containerColor = DarkSurface,
          contentColor = Amber400,
          elevation = FloatingActionButtonDefaults.elevation(4.dp),
          shape = CircleShape,
          modifier = Modifier
            .size(48.dp)
            .testTag("export_csv_fab")
        ) {
          Icon(
            imageVector = Icons.Default.FileDownload,
            contentDescription = "Export data",
            modifier = Modifier.size(20.dp)
          )
        }

        if (showScrollToTop) {
          FloatingActionButton(
            onClick = {
              coroutineScope.launch {
                listState.animateScrollToItem(0)
              }
            },
            containerColor = Amber400,
            contentColor = Amber950,
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            shape = CircleShape,
            modifier = Modifier
              .size(48.dp)
              .testTag("scroll_to_top_fab")
          ) {
            Icon(
              imageVector = Icons.Default.KeyboardArrowUp,
              contentDescription = "Scroll to top",
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.TopCenter
    ) {
      LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 640.dp)
      ) {
        // 1. Header Section
        item(key = "header") {
          HeaderSection(
            uncheckedCount = stats.unchecked,
            isScanning = isScanning,
            onAddSite = { viewModel.openAddSiteDialog() },
            onScanAll = { viewModel.startScanAll() },
            onSyncCloud = { viewModel.syncWithCloud() }
          )
        }

        // 2. Scan Progress Card
        item(key = "scan_progress") {
          ScanProgressCard(
            total = stats.total,
            checked = stats.total - stats.unchecked,
            isScanning = isScanning,
            currentDomain = scanningDomain,
            onPauseResume = {
              if (isScanning) viewModel.pauseScan() else viewModel.startScanAll()
            }
          )
        }

        // 3. Stats Grid
        item(key = "stats_grid") {
          StatsGrid(stats = stats)
        }

        // 4. Search Bar
        item(key = "search_bar") {
          SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setSearch(it) }
          )
        }

        // 5. Filter & Sort Row
        item(key = "filter_sort_row") {
          FilterSortRow(
            currentSort = currentSort,
            onSortSelected = { viewModel.setSort(it) },
            currentFilter = currentFilter,
            onFilterSelected = { viewModel.setFilter(it) }
          )
        }

        // 6. Section Header
        item(key = "section_header") {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Text(
              text = buildAnnotatedString {
                append("Sorted by ")
                withStyle(
                  SpanStyle(
                    fontFamily = InstrumentSerifFamily,
                    fontStyle = FontStyle.Italic,
                    color = Amber400,
                    fontSize = 24.sp
                  )
                ) {
                  append(currentSort.shortLabel)
                }
              },
              fontFamily = InstrumentSerifFamily,
              fontSize = 22.sp,
              color = ForegroundWhite
            )

            Text(
              text = "${rankedSites.size} ${if (rankedSites.size == 1) "SITE" else "SITES"}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.3.sp,
              color = MutedForeground
            )
          }
        }

        // 7. Ranked Bonus Sites List
        if (rankedSites.isEmpty()) {
          item(key = "empty_ranked") {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurface)
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = if (searchQuery.isNotEmpty() || currentFilter != FilterOption.ALL)
                    "No sites match that search"
                  else "No ranked sites yet",
                  fontFamily = InstrumentSerifFamily,
                  fontSize = 20.sp,
                  color = ForegroundWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = if (searchQuery.isNotEmpty() || currentFilter != FilterOption.ALL)
                    "Try a different filter or search term."
                  else "Add a site or run a scan to start building the ranking.",
                  fontSize = 13.sp,
                  color = MutedForeground,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        } else {
          itemsIndexed(
            items = rankedSites,
            key = { _, site -> site.id }
          ) { index, site ->
            BonusSiteCard(
              site = site,
              rank = index + 1,
              isRescanning = scanningDomain == site.domain,
              onEdit = { viewModel.openEditSiteDialog(it) },
              onRescan = { viewModel.rescanSite(it) },
              onClick = { viewModel.openSiteDetail(it) },
              onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
          }
        }

        // 8. Collapsible Unranked / No Bonus Section
        item(key = "unranked_section") {
          UnrankedSection(
            isExpanded = isUnrankedExpanded,
            onToggleExpand = { viewModel.toggleUnrankedExpanded() },
            sites = unrankedSites,
            rescanningDomain = scanningDomain,
            onEdit = { viewModel.openEditSiteDialog(it) },
            onRescan = { viewModel.rescanSite(it) },
            onClick = { viewModel.openSiteDetail(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) }
          )
        }

        // 9. Disclaimer Notice
        item(key = "disclaimer") {
          Text(
            text = "18+ only. Offers change without notice — always check the terms on the site itself.",
            fontSize = 11.sp,
            color = MutedForegroundSubtle,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp, bottom = 24.dp)
          )
        }
      }
    }
  }

  // Dialogs
  if (showAddEditDialog) {
    AddEditSiteDialog(
      site = editingSite,
      onDismiss = { viewModel.closeDialogs() },
      onSave = { name, domain, category, referralUrl, maxWithdrawal, depositRequired, turnover, status, scanNote ->
        viewModel.saveSite(
          name = name,
          domain = domain,
          category = category,
          referralUrl = referralUrl,
          maxWithdrawal = maxWithdrawal,
          depositRequired = depositRequired,
          turnover = turnover,
          status = status,
          scanNote = scanNote
        )
      }
    )
  }

  if (viewingSite != null) {
    SiteDetailSheet(
      site = viewingSite!!,
      onDismiss = { viewModel.closeDialogs() },
      onEdit = {
        viewModel.closeDialogs()
        viewModel.openEditSiteDialog(it)
      },
      onRescan = { viewModel.rescanSite(it) }
    )
  }

  if (showExportDialog) {
    ExportDialog(
      csvData = exportCsvContent,
      onDismiss = { showExportDialog = false }
    )
  }
}
