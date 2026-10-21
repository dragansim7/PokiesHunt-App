package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BonusSiteEntity
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber950
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InstrumentSerifFamily
import com.example.ui.theme.MutedForeground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SiteDetailSheet(
  site: BonusSiteEntity,
  onDismiss: () -> Unit,
  onEdit: (BonusSiteEntity) -> Unit,
  onRescan: (BonusSiteEntity) -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = DarkSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp))
        .padding(20.dp)
        .testTag("site_detail_sheet")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = site.name,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = ForegroundWhite
            )
            Text(
              text = site.domain,
              fontSize = 12.sp,
              color = Amber400
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MutedForeground
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bonus Metrics Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(DarkSurfaceVariant)
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "MAX WITHDRAWAL",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MutedForeground
              )
              Text(
                text = if (site.max_withdrawal > 0) "A$${site.max_withdrawal.toInt()}" else "—",
                fontFamily = InstrumentSerifFamily,
                fontSize = 24.sp,
                color = Amber400
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(DarkSurfaceVariant)
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "TURNOVER",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MutedForeground
              )
              Text(
                text = site.turnover,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForegroundWhite
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status & Deposit
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (site.deposit_required) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Amber400.copy(alpha = 0.12f))
                .border(1.dp, Amber400.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "Deposit required",
                fontSize = 11.sp,
                color = Amber300,
                fontWeight = FontWeight.Medium
              )
            }
          } else {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(EmeraldBg)
                .border(1.dp, EmeraldBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "No deposit needed",
                fontSize = 11.sp,
                color = Emerald400,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.05f))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = if (site.status == "bonus") "Share Bonus Active" else "No Bonus Verified",
              fontSize = 11.sp,
              color = MutedForeground
            )
          }
        }

        // Live Telemetry Details
        Spacer(modifier = Modifier.height(14.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "TELEMETRY & EXTRACTION AUDIT",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = Amber400
            )
            if (site.http_status != null && site.http_status > 0) {
              Text(
                text = "HTTP Status: ${site.http_status} OK",
                fontSize = 12.sp,
                color = ForegroundWhite
              )
            }
            if (site.response_time_ms != null && site.response_time_ms > 0) {
              Text(
                text = "Response Latency: ${site.response_time_ms} ms",
                fontSize = 12.sp,
                color = ForegroundWhite
              )
            }
            if (site.last_checked != null && site.last_checked > 0) {
              val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                .format(Date(site.last_checked))
              Text(
                text = "Last Checked: $dateStr",
                fontSize = 12.sp,
                color = MutedForeground
              )
            }
            if (!site.scan_note.isNullOrBlank()) {
              Text(
                text = "Notes: ${site.scan_note}",
                fontSize = 12.sp,
                color = MutedForeground
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val clip = ClipData.newPlainText("Referral Link", site.referral_url)
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Copied referral link!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ForegroundWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy", fontSize = 12.sp)
          }

          Button(
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(site.referral_url))
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.weight(1.3f),
            colors = ButtonDefaults.buttonColors(
              containerColor = Amber400,
              contentColor = Amber950
            )
          ) {
            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Link", fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              onDismiss()
              onEdit(site)
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedForeground),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Edit", fontSize = 12.sp)
          }

          OutlinedButton(
            onClick = {
              onDismiss()
              onRescan(site)
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber300),
            border = androidx.compose.foundation.BorderStroke(1.dp, Amber400.copy(alpha = 0.3f))
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Rescan", fontSize = 12.sp)
          }
        }
      }
    }
  }
}
