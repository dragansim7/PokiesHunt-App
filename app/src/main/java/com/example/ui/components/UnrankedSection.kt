package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BonusSiteEntity
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.MutedForeground
import com.example.ui.theme.MutedForegroundSubtle

@Composable
fun UnrankedSection(
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  sites: List<BonusSiteEntity>,
  rescanningDomain: String?,
  onEdit: (BonusSiteEntity) -> Unit,
  onRescan: (BonusSiteEntity) -> Unit,
  onClick: (BonusSiteEntity) -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val rotation by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    label = "chevronRotation"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(DarkSurface)
      .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
  ) {
    Column {
      // Accordion Trigger Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() }
          .padding(horizontal = 16.dp, vertical = 14.dp)
          .testTag("unranked_accordion_trigger"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "No bonus found",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = ForegroundWhite
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.06f))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${sites.size}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = MutedForeground
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "sites not ranked",
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            color = MutedForegroundSubtle
          )
        }

        Icon(
          imageVector = Icons.Default.KeyboardArrowDown,
          contentDescription = if (isExpanded) "Collapse" else "Expand",
          tint = MutedForeground,
          modifier = Modifier
            .size(20.dp)
            .rotate(rotation)
        )
      }

      // Collapsible Content List
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (sites.isEmpty()) {
            Text(
              text = "All sites have been audited or have active bonuses.",
              fontSize = 12.sp,
              color = MutedForeground,
              modifier = Modifier.padding(8.dp)
            )
          } else {
            // Show up to 40 unranked sites with pagination if long
            sites.take(50).forEach { site ->
              BonusSiteCard(
                site = site,
                rank = null,
                isRescanning = rescanningDomain == site.domain,
                onEdit = onEdit,
                onRescan = onRescan,
                onClick = onClick,
                onToggleFavorite = onToggleFavorite
              )
            }
            if (sites.size > 50) {
              Text(
                text = "Showing 50 of ${sites.size} unranked sites. Use search to find specific domains.",
                fontSize = 11.sp,
                color = MutedForegroundSubtle,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
              )
            }
          }
        }
      }
    }
  }
}
