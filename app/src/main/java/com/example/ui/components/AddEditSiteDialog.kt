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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BonusSiteEntity
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber950
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InstrumentSerifFamily
import com.example.ui.theme.MutedForeground

@Composable
fun AddEditSiteDialog(
  site: BonusSiteEntity?,
  onDismiss: () -> Unit,
  onSave: (
    name: String,
    domain: String,
    category: String?,
    referralUrl: String,
    maxWithdrawal: Double,
    depositRequired: Boolean,
    turnover: String,
    status: String,
    scanNote: String?
  ) -> Unit
) {
  var name by remember { mutableStateOf(site?.name.orEmpty()) }
  var domain by remember { mutableStateOf(site?.domain.orEmpty()) }
  var category by remember { mutableStateOf(site?.category.orEmpty()) }
  var referralUrl by remember { mutableStateOf(site?.referral_url.orEmpty()) }
  var maxWithdrawalStr by remember {
    mutableStateOf(if (site != null && site.max_withdrawal > 0) site.max_withdrawal.toInt().toString() else "")
  }
  var depositRequired by remember { mutableStateOf(site?.deposit_required ?: false) }
  var turnover by remember { mutableStateOf(site?.turnover ?: "—") }
  var status by remember { mutableStateOf(site?.status ?: "bonus") }
  var scanNote by remember { mutableStateOf(site?.scan_note.orEmpty()) }

  val textFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ForegroundWhite,
    unfocusedTextColor = ForegroundWhite,
    focusedBorderColor = Amber400,
    unfocusedBorderColor = DarkCardBorder,
    focusedLabelColor = Amber400,
    unfocusedLabelColor = MutedForeground,
    cursorColor = Amber400
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DarkSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
        .padding(18.dp)
        .testTag("add_edit_site_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = if (site == null) "Add Site" else "Edit Site",
          fontFamily = InstrumentSerifFamily,
          fontSize = 24.sp,
          color = Amber400
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Site Name") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = domain,
          onValueChange = { domain = it },
          label = { Text("Domain (e.g. pokies88.com)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = referralUrl,
          onValueChange = { referralUrl = it },
          label = { Text("Referral Link URL") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = maxWithdrawalStr,
            onValueChange = { maxWithdrawalStr = it },
            label = { Text("Max Withdrawal ($)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = textFieldColors
          )

          OutlinedTextField(
            value = turnover,
            onValueChange = { turnover = it },
            label = { Text("Turnover (e.g. x6)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = textFieldColors
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Deposit Required Switch
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Deposit Required",
            fontSize = 13.sp,
            color = ForegroundWhite
          )
          Switch(
            checked = depositRequired,
            onCheckedChange = { depositRequired = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Amber400,
              checkedTrackColor = Amber950,
              uncheckedThumbColor = MutedForeground,
              uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("bonus" to "Bonus Found", "no_bonus" to "No Bonus", "unchecked" to "Unchecked").forEach { (valKey, labelText) ->
            val isSelected = status == valKey
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Amber400.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f))
                .border(
                  1.dp,
                  if (isSelected) Amber400 else DarkCardBorder,
                  RoundedCornerShape(10.dp)
                )
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = labelText,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Amber400 else MutedForeground
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = scanNote,
          onValueChange = { scanNote = it },
          label = { Text("Bonus terms / Scan notes") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3,
          colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedForeground),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
          ) {
            Text("Cancel")
          }

          Button(
            onClick = {
              val parsedWithdrawal = maxWithdrawalStr.toDoubleOrNull() ?: 0.0
              onSave(
                name,
                domain,
                category.takeIf { it.isNotBlank() },
                referralUrl,
                parsedWithdrawal,
                depositRequired,
                turnover,
                status,
                scanNote.takeIf { it.isNotBlank() }
              )
            },
            enabled = domain.isNotBlank(),
            modifier = Modifier
              .weight(1f)
              .testTag("save_site_submit_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Amber400,
              contentColor = Amber950
            )
          ) {
            Text(if (site == null) "Add Site" else "Save")
          }
        }
      }
    }
  }
}
