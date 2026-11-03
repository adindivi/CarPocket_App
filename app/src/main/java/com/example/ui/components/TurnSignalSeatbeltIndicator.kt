package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TurnSignal
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TurnSignalSeatbeltIndicator(
  turnSignal: TurnSignal,
  isSeatbeltFastened: Boolean,
  onLeftSignalClick: () -> Unit,
  onRightSignalClick: () -> Unit,
  onSeatbeltToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "blinker")

  val blinkAlpha by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Directional Turn Signal Status Pill (Clean 1-row layout)
      val isLeftActive = turnSignal == TurnSignal.LEFT
      val isRightActive = turnSignal == TurnSignal.RIGHT
      val isAnyActive = isLeftActive || isRightActive

      Box(
        modifier = Modifier
          .weight(1.1f)
          .height(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (isAnyActive) CyanGlow else LightSurfaceSubtle)
          .border(
            1.dp,
            if (isAnyActive) CyanNeon.copy(alpha = 0.5f) else LightCardBorder,
            RoundedCornerShape(8.dp)
          )
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left signal trigger
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .clickable { onLeftSignalClick() }
              .testTag("left_blinker_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "좌측 깜빡이",
              tint = if (isLeftActive) CyanNeon.copy(alpha = blinkAlpha) else TextMuted,
              modifier = Modifier.size(15.dp)
            )
          }

          Text(
            text = when {
              isLeftActive -> "좌회전 깜빡이"
              isRightActive -> "우회전 깜빡이"
              else -> "깜빡이 대기 중"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAnyActive) CyanNeon else TextSecondary,
            maxLines = 1,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )

          // Right signal trigger
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .clickable { onRightSignalClick() }
              .testTag("right_blinker_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "우측 깜빡이",
              tint = if (isRightActive) CyanNeon.copy(alpha = blinkAlpha) else TextMuted,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }

      // 2. Seatbelt Status Pill (Clean 1-row layout)
      Box(
        modifier = Modifier
          .weight(0.95f)
          .height(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSeatbeltFastened) EmeraldGlow.copy(alpha = 0.6f) else CrimsonGlow)
          .border(
            1.dp,
            if (isSeatbeltFastened) EmeraldSafe.copy(alpha = 0.35f) else CrimsonAlert.copy(alpha = 0.5f),
            RoundedCornerShape(8.dp)
          )
          .clickable { onSeatbeltToggle() }
          .padding(horizontal = 8.dp)
          .testTag("seatbelt_button"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AirlineSeatReclineNormal,
            contentDescription = "안전벨트 상태",
            tint = if (isSeatbeltFastened) EmeraldSafe else CrimsonAlert,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = if (isSeatbeltFastened) "안전벨트 착용 중" else "벨트를 매주세요",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSeatbeltFastened) EmeraldSafe else CrimsonAlert,
            softWrap = false,
            maxLines = 1,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }
  }
}
