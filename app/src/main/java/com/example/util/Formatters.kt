package com.example.util

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import java.text.NumberFormat
import java.util.Locale

/**
 * Universal Fintech & Telematics Formatters
 * Enforces DRY principles across all Compose UI screens.
 */
object Formatters {

  private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

  /** Formats an integer with thousands separator (e.g., 14,250) */
  fun formatNumber(value: Number): String = numberFormat.format(value)

  /** Formats currency in Korean Won (e.g., "196,800원") */
  fun formatWon(amountWon: Int): String = "${formatNumber(amountWon)}원"

  /** Formats savings with leading minus sign (e.g., "-196,800원") */
  fun formatSavingsWon(amountWon: Int): String = "-${formatNumber(amountWon)}원"

  /** Formats reward points (e.g., "14,250 P") */
  fun formatPoints(points: Int): String = "${formatNumber(points)} P"

  /** Formats distance in km (e.g., "42,350 km") */
  fun formatOdometerKm(km: Int): String = "${formatNumber(km)} km"

  /** Formats decimal distance (e.g., "18.4km") */
  fun formatDistanceKm(km: Float): String = String.format(Locale.KOREA, "%.1fkm", km)
}

/** Extension functions for clean, readable syntax */
fun Int.toFormattedComma(): String = Formatters.formatNumber(this)
fun Int.toCurrencyWon(): String = Formatters.formatWon(this)
fun Int.toSavingsWon(): String = Formatters.formatSavingsWon(this)
fun Int.toFormattedPoints(): String = Formatters.formatPoints(this)
fun Int.toOdometerKm(): String = Formatters.formatOdometerKm(this)
fun Float.toFormattedKm(): String = Formatters.formatDistanceKm(this)

/**
 * Default TextStyle with Samsung One UI baseline centering (includeFontPadding = false)
 */
@Suppress("DEPRECATION")
val NoFontPaddingTextStyle: TextStyle = TextStyle(
  platformStyle = PlatformTextStyle(includeFontPadding = false)
)
