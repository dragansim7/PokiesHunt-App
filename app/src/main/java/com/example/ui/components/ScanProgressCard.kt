package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.MutedForeground

@Composable
fun ScanProgressCard(
  total: Int,
  checked: Int,
  isScanning: Boolean,
  currentDomain: String?,
  onPauseResume: () -> Unit,
  modifier: Modifier = Modifier
) {
  val percent = if (total > 0) ((checked.toFloat() / total.toFloat()) * 100).toInt().coerceIn(0, 100) else 0

  val statusTitle = when {
    isScanning -> "Scanning links"
    total > 0 && checked >= total -> "Scan complete"
    else -> "Scan paused"
  }

  val infiniteTransition = rememberInfiniteTransition(label = "spin")
  val angle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = LinearEasing)
    ),
    label = "spinAngle"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(DarkSurface)
      .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
      .padding(horizontal = 16.dp, vertical = 14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(
                if (isScanning) Amber400.copy(alpha = 0.12f)
                else Color.White.copy(alpha = 0.04f)
              )
              .border(
                1.dp,
                if (isScanning) Amber400.copy(alpha = 0.4f) else DarkCardBorder,
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = null,
              tint = if (isScanning) Amber300 else MutedForeground,
              modifier = Modifier
                .size(18.dp)
                .rotate(if (isScanning) angle else 0f)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = statusTitle,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = ForegroundWhite
            )
            Text(
              text = "$percent% OF THE INDEX CHECKED",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              color = MutedForeground
            )
          }
        }

        IconButton(
          onClick = onPauseResume,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, DarkCardBorder, CircleShape)
            .testTag("scan_pause_resume_button")
        ) {
          Icon(
            imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isScanning) "Pause scan" else "Resume scan",
            tint = ForegroundWhite,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      AnimatedVisibility(visible = !currentDomain.isNullOrBlank()) {
        Text(
          text = "Testing: ${currentDomain.orEmpty()}",
          fontSize = 11.sp,
          color = Amber300.copy(alpha = 0.85f),
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress Track
      LinearProgressIndicator(
        progress = { percent / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = Amber400,
        trackColor = Color.White.copy(alpha = 0.08f),
      )
    }
  }
}
