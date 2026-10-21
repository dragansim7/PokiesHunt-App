package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FilterOption
import com.example.ui.SortOption
import com.example.ui.theme.Amber400
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.MutedForeground

@Composable
fun FilterSortRow(
  currentSort: SortOption,
  onSortSelected: (SortOption) -> Unit,
  currentFilter: FilterOption,
  onFilterSelected: (FilterOption) -> Unit,
  modifier: Modifier = Modifier
) {
  var sortExpanded by remember { mutableStateOf(false) }
  var filterExpanded by remember { mutableStateOf(false) }

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Sort Picker
    Box(modifier = Modifier.weight(1.3f)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.03f))
          .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
          .clickable { sortExpanded = true }
          .padding(horizontal = 12.dp)
          .testTag("sort_picker_trigger"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Sort,
          contentDescription = null,
          tint = Amber400.copy(alpha = 0.85f),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = currentSort.shortLabel,
          fontSize = 12.sp,
          color = ForegroundWhite,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          modifier = Modifier.weight(1f)
        )
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = null,
          tint = MutedForeground,
          modifier = Modifier.size(18.dp)
        )
      }

      DropdownMenu(
        expanded = sortExpanded,
        onDismissRequest = { sortExpanded = false },
        modifier = Modifier
          .background(DarkSurfaceVariant)
          .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
      ) {
        SortOption.entries.forEach { option ->
          DropdownMenuItem(
            text = {
              Text(
                text = option.label,
                fontSize = 13.sp,
                color = if (option == currentSort) Amber400 else ForegroundWhite
              )
            },
            onClick = {
              onSortSelected(option)
              sortExpanded = false
            },
            colors = MenuDefaults.itemColors()
          )
        }
      }
    }

    // Filter Picker
    Box(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.03f))
          .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
          .clickable { filterExpanded = true }
          .padding(horizontal = 12.dp)
          .testTag("filter_picker_trigger"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.FilterList,
          contentDescription = null,
          tint = Amber400.copy(alpha = 0.85f),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = currentFilter.label,
          fontSize = 12.sp,
          color = ForegroundWhite,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          modifier = Modifier.weight(1f)
        )
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = null,
          tint = MutedForeground,
          modifier = Modifier.size(18.dp)
        )
      }

      DropdownMenu(
        expanded = filterExpanded,
        onDismissRequest = { filterExpanded = false },
        modifier = Modifier
          .background(DarkSurfaceVariant)
          .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
      ) {
        FilterOption.entries.forEach { option ->
          DropdownMenuItem(
            text = {
              Text(
                text = option.label,
                fontSize = 13.sp,
                color = if (option == currentFilter) Amber400 else ForegroundWhite
              )
            },
            onClick = {
              onFilterSelected(option)
              filterExpanded = false
            },
            colors = MenuDefaults.itemColors()
          )
        }
      }
    }
  }
}
