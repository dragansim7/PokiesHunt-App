package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class SiteStats(
  val total: Int = 0,
  val bonus: Int = 0,
  val noBonus: Int = 0,
  val unchecked: Int = 0
)

@Dao
interface BonusSiteDao {

  @Query("SELECT COUNT(*) FROM bonus_sites")
  suspend fun getCount(): Int

  @Query("SELECT * FROM bonus_sites WHERE status = 'bonus' ORDER BY max_withdrawal DESC, name ASC")
  fun getRankedBonusSitesFlow(): Flow<List<BonusSiteEntity>>

  @Query("SELECT * FROM bonus_sites WHERE status != 'bonus' ORDER BY status ASC, name ASC")
  fun getUnrankedSitesFlow(): Flow<List<BonusSiteEntity>>

  @Query("SELECT * FROM bonus_sites ORDER BY name ASC")
  fun getAllSitesFlow(): Flow<List<BonusSiteEntity>>

  @Query("SELECT * FROM bonus_sites WHERE id = :id LIMIT 1")
  suspend fun getSiteById(id: String): BonusSiteEntity?

  @Query("SELECT * FROM bonus_sites WHERE domain = :domain LIMIT 1")
  suspend fun getSiteByDomain(domain: String): BonusSiteEntity?

  @Query("SELECT * FROM bonus_sites WHERE status = 'unchecked' ORDER BY id ASC LIMIT :limit")
  suspend fun getUncheckedSites(limit: Int): List<BonusSiteEntity>

  @Query("SELECT * FROM bonus_sites ORDER BY id ASC")
  suspend fun getAllSitesList(): List<BonusSiteEntity>

  @Query("SELECT COUNT(*) FROM bonus_sites")
  fun getTotalCountFlow(): Flow<Int>

  @Query("SELECT COUNT(*) FROM bonus_sites WHERE status = 'bonus'")
  fun getBonusCountFlow(): Flow<Int>

  @Query("SELECT COUNT(*) FROM bonus_sites WHERE status = 'no_bonus'")
  fun getNoBonusCountFlow(): Flow<Int>

  @Query("SELECT COUNT(*) FROM bonus_sites WHERE status = 'unchecked'")
  fun getUncheckedCountFlow(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(sites: List<BonusSiteEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(site: BonusSiteEntity)

  @Update
  suspend fun update(site: BonusSiteEntity)

  @Delete
  suspend fun delete(site: BonusSiteEntity)

  @Query("""
    UPDATE bonus_sites 
    SET status = :status,
        max_withdrawal = :maxWithdrawal,
        deposit_required = :depositRequired,
        turnover = :turnover,
        scan_note = :scanNote,
        last_checked = :lastChecked,
        response_time_ms = :responseTimeMs,
        http_status = :httpStatus
    WHERE id = :id
  """)
  suspend fun updateScanResult(
    id: String,
    status: String,
    maxWithdrawal: Double,
    depositRequired: Boolean,
    turnover: String,
    scanNote: String?,
    lastChecked: Long,
    responseTimeMs: Long?,
    httpStatus: Int?
  )

  @Query("""
    UPDATE bonus_sites
    SET is_favorite = NOT is_favorite
    WHERE id = :id
  """)
  suspend fun toggleFavorite(id: String)
}
