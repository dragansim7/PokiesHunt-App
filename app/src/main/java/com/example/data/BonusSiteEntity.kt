package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "bonus_sites",
  indices = [
    Index(value = ["domain"], unique = true),
    Index(value = ["status"]),
    Index(value = ["max_withdrawal"])
  ]
)
data class BonusSiteEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val domain: String,
  val category: String? = null,
  val referral_url: String,
  val referral_code: String? = null,
  val max_withdrawal: Double = 0.0,
  val deposit_required: Boolean = false,
  val turnover: String = "—",
  val status: String = "unchecked", // "bonus", "no_bonus", "unchecked"
  val scan_note: String? = null,
  val last_checked: Long? = null,
  val response_time_ms: Long? = null,
  val http_status: Int? = null,
  val is_favorite: Boolean = false
)
