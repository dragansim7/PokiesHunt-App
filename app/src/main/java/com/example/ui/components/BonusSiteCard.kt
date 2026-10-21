package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BonusSiteEntity
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InstrumentSerifFamily
import com.example.ui.theme.MutedForeground
import com.example.ui.theme.MutedForegroundSubtle

@Composable
fun BonusSiteCard(
  site: BonusSiteEntity,
  rank: Int?,
  isRescanning: Boolean,
  onEdit: (BonusSiteEntity) -> Unit,
  onRescan: (BonusSiteEntity) -> Unit,
  onClick: (BonusSiteEntity) -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val infiniteTransition = rememberInfiniteTransition(label = "spin")
  val angle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = LinearEasing)
    ),
    label = "spinAngle"
  )

  val formattedWithdrawal = if (site.max_withdrawal > 0) {
    "A$${site.max_withdrawal.toInt()}"
  } else {
    "—"
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(DarkSurface)
      .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
      .clickable { onClick(site) }
      .padding(14.dp)
      .testTag("site_card_${site.id}")
  ) {
    Column {
      // Top Row: Rank, Title, Max Withdrawal
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        if (rank != null) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$rank",
              fontFamily = InstrumentSerifFamily,
              fontSize = 16.sp,
              color = if (rank <= 3) Amber400 else MutedForeground
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = site.name,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = ForegroundWhite,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (site.status == "bonus") {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Amber400.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "SHARE BONUS",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.8.sp,
                  color = Amber300
                )
              }
            }
          }

          Text(
            text = site.domain,
            fontSize = 12.sp,
            color = MutedForeground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Max Withdrawal display
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = formattedWithdrawal,
            fontFamily = InstrumentSerifFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            color = if (site.max_withdrawal > 0) Amber400 else MutedForegroundSubtle
          )
          Text(
            text = "MAX WITHDRAWAL",
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = MutedForeground
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tags Row: Deposit & Turnover & Telemetry
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Deposit Tag
        if (site.deposit_required) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Amber400.copy(alpha = 0.1f))
              .border(1.dp, Amber400.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "Deposit required",
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              color = Amber300
            )
          }
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(EmeraldBg)
              .border(1.dp, EmeraldBorder, RoundedCornerShape(12.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "No deposit",
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              color = Emerald400
            )
          }
        }

        // Turnover Tag
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "Turnover ${site.turnover}",
            fontSize = 10.sp,
            color = MutedForeground
          )
        }

        // Response Latency if checked
        if (site.response_time_ms != null && site.response_time_ms > 0) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.03f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "${site.response_time_ms}ms",
              fontSize = 10.sp,
              color = MutedForegroundSubtle
            )
          }
        }
      }

      // Note snippet if present
      if (!site.scan_note.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = site.scan_note,
          fontSize = 11.sp,
          color = MutedForeground,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Action Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Favorite toggle
        IconButton(
          onClick = { onToggleFavorite(site.id) },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = if (site.is_favorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
            contentDescription = "Favorite",
            tint = if (site.is_favorite) Amber400 else MutedForeground,
            modifier = Modifier.size(16.dp)
          )
        }

        // Copy referral link
        IconButton(
          onClick = {
            val clip = ClipData.newPlainText("Referral Link", site.referral_url)
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied referral link!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy referral link",
            tint = MutedForeground,
            modifier = Modifier.size(16.dp)
          )
        }

        // Open referral URL in browser
        IconButton(
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(site.referral_url))
              context.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
            }
          },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Open referral link",
            tint = Amber300,
            modifier = Modifier.size(16.dp)
          )
        }

        // Edit
        IconButton(
          onClick = { onEdit(site) },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit site",
            tint = MutedForeground,
            modifier = Modifier.size(16.dp)
          )
        }

        // Rescan
        IconButton(
          onClick = { onRescan(site) },
          enabled = !isRescanning,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Rescan site",
            tint = if (isRescanning) Amber300 else MutedForeground,
            modifier = Modifier
              .size(16.dp)
              .rotate(if (isRescanning) angle else 0f)
          )
        }
      }
    }
  }
}
