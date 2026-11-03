package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinBg
import com.example.ui.theme.PurpleIndigo
import com.example.ui.theme.PurpleIndigoLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenLight
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TossBlue
import com.example.ui.theme.TossBlueLight
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight

/**
 * Reward Category for Duotone Vector Artwork (Replacing Raw Emojis).
 */
enum class RewardArtworkType {
  FUEL,
  CAR_WASH,
  COFFEE,
  EV_CHARGING,
  WELCOME
}

/**
 * Pure Vector-Crafted Modern Duotone Icon for Rewards & Challenges.
 * Eliminates cheap emojis (⛽, 🧼, ☕, ⚡) with precision Fintech Vector styling.
 */
@Composable
fun DuotoneRewardBadge(
  type: RewardArtworkType,
  modifier: Modifier = Modifier,
  size: Dp = 38.dp,
  iconSize: Dp = 20.dp
) {
  val (bgColor, strokeColor, iconColor, icon) = when (type) {
    RewardArtworkType.FUEL -> Quadruple(
      TossBlueLight,
      TossBlue.copy(alpha = 0.35f),
      TossBlue,
      Icons.Default.LocalGasStation
    )
    RewardArtworkType.CAR_WASH -> Quadruple(
      SafeGreenLight,
      SafeGreen.copy(alpha = 0.35f),
      SafeGreen,
      Icons.Default.WaterDrop
    )
    RewardArtworkType.COFFEE -> Quadruple(
      WarningAmberLight,
      WarningAmber.copy(alpha = 0.35f),
      WarningAmber,
      Icons.Default.Coffee
    )
    RewardArtworkType.EV_CHARGING -> Quadruple(
      PurpleIndigoLight,
      PurpleIndigo.copy(alpha = 0.35f),
      PurpleIndigo,
      Icons.Default.Bolt
    )
    RewardArtworkType.WELCOME -> Quadruple(
      GoldCoinBg,
      GoldCoin.copy(alpha = 0.35f),
      GoldCoin,
      Icons.Default.Savings
    )
  }

  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(1.dp, strokeColor, RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = iconColor,
      modifier = Modifier.size(iconSize)
    )
  }
}

/**
 * Helper tuple for clean unpacking
 */
private data class Quadruple<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)

/**
 * 2.5D Geometric CarPocket Vault / Coin Graphic
 * Canvas-rendered metallic coin with high-trust fintech finish.
 */
@Composable
fun CarPocketCoinVaultIcon(
  modifier: Modifier = Modifier,
  size: Dp = 44.dp
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(GoldCoinBg)
      .border(1.5.dp, GoldCoin.copy(alpha = 0.4f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size * 0.7f)) {
      val canvasWidth = this.size.width
      val canvasHeight = this.size.height
      val centerOffset = Offset(canvasWidth / 2f, canvasHeight / 2f)
      val radius = (canvasWidth / 2f) - 2.dp.toPx()

      // Coin base radial gradient
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFFDE68A), Color(0xFFF59E0B)),
          center = Offset(centerOffset.x - radius * 0.3f, centerOffset.y - radius * 0.3f),
          radius = radius * 1.2f
        ),
        radius = radius,
        center = centerOffset
      )

      // Inner coin rim
      drawCircle(
        color = Color(0xFFD97706),
        radius = radius * 0.75f,
        center = centerOffset,
        style = Stroke(width = 1.5.dp.toPx())
      )

      // Vertical Coin Center bar / 'P' or '₩' subtle cross
      drawLine(
        color = Color(0xFFB45309),
        start = Offset(centerOffset.x, centerOffset.y - radius * 0.45f),
        end = Offset(centerOffset.x, centerOffset.y + radius * 0.45f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
      )
    }
  }
}
