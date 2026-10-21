package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber950
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InstrumentSerifFamily
import com.example.ui.theme.MutedForeground

@Composable
fun HeaderSection(
  uncheckedCount: Int,
  isScanning: Boolean,
  onAddSite: () -> Unit,
  onScanAll: () -> Unit,
  onSyncCloud: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Column(
    modifier = modifier.fillMaxWidth()
  ) {
    // Top Live Pill
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .scale(pulseScale)
          .clip(CircleShape)
          .background(Amber400)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "REFERRAL BONUS INDEX",
        color = Amber400.copy(alpha = 0.9f),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp
      )
      Spacer(modifier = Modifier.weight(1f))
      IconButton(
        onClick = onSyncCloud,
        modifier = Modifier
          .size(36.dp)
          .testTag("sync_cloud_button")
      ) {
        Icon(
          imageVector = Icons.Default.CloudSync,
          contentDescription = "Sync from cloud",
          tint = Amber400.copy(alpha = 0.8f),
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // Main App Title
    Text(
      text = buildAnnotatedString {
        append("Share Bonus ")
        withStyle(
          SpanStyle(
            fontFamily = InstrumentSerifFamily,
            fontStyle = FontStyle.Italic,
            color = Amber400,
            fontSize = 42.sp
          )
        ) {
          append("Rankings")
        }
      },
      fontFamily = InstrumentSerifFamily,
      fontSize = 38.sp,
      fontWeight = FontWeight.Normal,
      color = ForegroundWhite,
      lineHeight = 44.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Referral share invite bonuses, ranked by the highest max withdrawal.",
      color = MutedForeground,
      fontSize = 14.sp,
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Action Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedButton(
        onClick = onAddSite,
        modifier = Modifier
          .weight(1f)
          .height(44.dp)
          .testTag("add_site_button"),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = Color.White.copy(alpha = 0.04f),
          contentColor = ForegroundWhite
        )
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = Amber300
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Add site",
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      }

      val gradientBrush = Brush.verticalGradient(
        colors = listOf(Amber300, Amber500)
      )

      Button(
        onClick = onScanAll,
        enabled = !isScanning && uncheckedCount > 0,
        modifier = Modifier
          .weight(1.3f)
          .height(44.dp)
          .testTag("scan_all_button"),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Amber400,
          contentColor = Amber950,
          disabledContainerColor = Amber400.copy(alpha = 0.4f),
          disabledContentColor = Amber950.copy(alpha = 0.6f)
        )
      ) {
        if (isScanning) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = Amber950
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Scanning…",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        } else {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (uncheckedCount > 0) "Scan all $uncheckedCount" else "All scanned",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
