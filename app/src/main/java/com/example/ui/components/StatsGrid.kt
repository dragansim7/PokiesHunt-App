package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SiteStats
import com.example.ui.theme.Amber400
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InstrumentSerifFamily
import com.example.ui.theme.MutedForeground

@Composable
fun StatsGrid(
  stats: SiteStats,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatCard(
        label = "LINKS TRACKED",
        value = stats.total.toString(),
        isAmber = false,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        label = "SHARE BONUS FOUND",
        value = stats.bonus.toString(),
        isAmber = true,
        modifier = Modifier.weight(1f)
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatCard(
        label = "NO BONUS FOUND",
        value = stats.noBonus.toString(),
        isAmber = false,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        label = "NOT CHECKED YET",
        value = stats.unchecked.toString(),
        isAmber = false,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun StatCard(
  label: String,
  value: String,
  isAmber: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .background(DarkSurface)
      .border(
        1.dp,
        if (isAmber) Amber400.copy(alpha = 0.35f) else DarkCardBorder,
        RoundedCornerShape(18.dp)
      )
      .padding(horizontal = 16.dp, vertical = 14.dp)
  ) {
    if (isAmber) {
      // Glow light corner
      Box(
        modifier = Modifier
          .size(50.dp)
          .offset(x = 55.dp, y = (-20).dp)
          .clip(CircleShape)
          .background(Amber400.copy(alpha = 0.15f))
          .blur(16.dp)
      )
    }

    Column {
      Text(
        text = value,
        fontFamily = InstrumentSerifFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
        color = if (isAmber) Amber400 else ForegroundWhite
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.3.sp,
        color = MutedForeground
      )
    }
  }
}
