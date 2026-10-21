package com.example

import com.example.data.BonusSiteEntity
import com.example.ui.SortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testTurnoverParsingAndSorting() {
    val siteA = BonusSiteEntity(
      id = "1",
      name = "Pokies88",
      domain = "pokies88.com",
      referral_url = "https://pokies88.com/RF123",
      max_withdrawal = 88.0,
      turnover = "x6",
      deposit_required = false,
      status = "bonus"
    )

    val siteB = BonusSiteEntity(
      id = "2",
      name = "19rich",
      domain = "19rich.com",
      referral_url = "https://19rich.com/REF123",
      max_withdrawal = 120.0,
      turnover = "x10",
      deposit_required = true,
      status = "bonus"
    )

    val sites = listOf(siteA, siteB)
    val sortedByWithdrawal = sites.sortedByDescending { it.max_withdrawal }

    assertEquals("19rich", sortedByWithdrawal[0].name)
    assertEquals(120.0, sortedByWithdrawal[0].max_withdrawal, 0.01)
    assertEquals("Pokies88", sortedByWithdrawal[1].name)
  }

  @Test
  fun testDepositFilterLogic() {
    val freeSite = BonusSiteEntity(
      id = "1",
      name = "CrownAu",
      domain = "crownau.com",
      referral_url = "https://crownau.com/RF1",
      max_withdrawal = 30.0,
      deposit_required = false,
      status = "bonus"
    )
    val depositSite = BonusSiteEntity(
      id = "2",
      name = "123bet",
      domain = "123bet.club",
      referral_url = "https://123bet.club/RF2",
      max_withdrawal = 10.0,
      deposit_required = true,
      status = "bonus"
    )

    val all = listOf(freeSite, depositSite)
    val freeOnly = all.filter { !it.deposit_required }
    assertEquals(1, freeOnly.size)
    assertEquals("CrownAu", freeOnly[0].name)
  }
}
