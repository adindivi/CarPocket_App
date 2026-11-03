package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * CarPocket Modern Fintech Neutral & Human-Centric Design System
 * Inspired by Toss & Apple CarPlay HIG.
 * Zero cyber-neon glow, maximum readability, elegant slate typography.
 */

// 1. Foundation Neutral Backgrounds & Cards
val SlateBg = Color(0xFFF8FAFC)                 // Clean Daylight Slate Canvas
val SlateCard = Color(0xFFFFFFFF)               // Pure White Card Surface
val SlateCardElevated = Color(0xFFFFFFFF)
val SlatePill = Color(0xFFF1F5F9)               // Neutral Pill & Badge Capsule
val SlateBorder = Color(0xFFE2E8F0)             // 1px Subtle Card Outline
val SlateDivider = Color(0xFFE2E8F0)            // 1px Separation Line

// 2. Primary Brand Accent: Toss Signature Blue
val TossBlue = Color(0xFF3182F6)                // High-Trust Fintech Hero Blue
val TossBlueDark = Color(0xFF1B64DA)            // Active/Pressed State Blue
val TossBlueLight = Color(0xFFE8F3FF)           // 10% Soft Blue Tint Capsule

// 3. Status Accents (Restrained & Functional)
val SafeGreen = Color(0xFF10B981)               // Forest Mint (Safe Driving / Points)
val SafeGreenLight = Color(0xFFECFDF5)          // 10% Mint Tint Capsule
val WarningAmber = Color(0xFFF59E0B)            // Warm Safety Amber
val WarningAmberLight = Color(0xFFFFFBEB)       // 10% Amber Tint Capsule
val AlertCrimson = Color(0xFFEF4444)            // Clear Caution Red
val AlertCrimsonLight = Color(0xFFFEF2F2)       // 10% Red Tint Capsule

// 4. CarPocket Metallic & Wealth Accents
val GoldCoin = Color(0xFFF59E0B)                // Savings Coin Accent
val GoldCoinBg = Color(0xFFFEF3C7)              // Coin Capsule Background
val PurpleIndigo = Color(0xFF4F46E5)            // AI Feature Accent
val PurpleIndigoLight = Color(0xFFEEF2FF)

// 5. Typography Standards (WCAG AAA Contrast)
val SlateDark = Color(0xFF0F172A)               // Slate 900: Headline & Large Numbers
val SlateBody = Color(0xFF334155)               // Slate 700: Body & Informational Text
val SlateMuted = Color(0xFF64748B)              // Slate 500: Metadata & Sub-labels
val SlateSubtle = Color(0xFF94A3B8)             // Slate 400: Icons & Disabled Text
val TextOnAccent = Color(0xFFFFFFFF)            // Pure High-Contrast White

// 6. Backward Compatibility Aliases for Seamless Compilation
val LightAppBg = SlateBg
val LightSurface = SlateCard
val LightSurfaceElevated = SlateCardElevated
val LightSurfaceSubtle = SlatePill
val LightSurfaceAccentTint = TossBlueLight
val LightCardBorder = SlateBorder
val LightDivider = SlateDivider

val DarkNavyBg = SlateBg
val DarkSurface = SlateCard
val DarkSurfaceElevated = SlateCardElevated
val DarkSurfaceCard = SlatePill
val DarkCardBorder = SlateBorder
val DarkDivider = SlateDivider

val CyanNeon = TossBlue
val CyanGlow = TossBlueLight
val EmeraldSafe = SafeGreen
val EmeraldGlow = SafeGreenLight
val AmberWarning = WarningAmber
val AmberGlow = WarningAmberLight
val CrimsonAlert = AlertCrimson
val CrimsonGlow = AlertCrimsonLight
val GoldStar = GoldCoin
val GoldBadgeBg = GoldCoinBg
val PurpleAccent = PurpleIndigo
val PurpleGlow = PurpleIndigoLight

val TextPrimary = SlateDark
val TextSecondary = SlateBody
val TextMuted = SlateMuted
