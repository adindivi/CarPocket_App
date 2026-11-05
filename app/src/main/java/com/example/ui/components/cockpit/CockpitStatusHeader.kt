package com.example.ui.components.cockpit

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import com.example.viewmodel.CockpitUiState

@Composable
fun CockpitStatusHeader(
  state: CockpitUiState,
  onToggleMute: () -> Unit,
  onToggleBeepMode: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      // 1행: 가변 제목 ── 고정 수치 뱃지 + 비프음 모드 + 음성 버튼
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = state.vehicleModel,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          modifier = Modifier.weight(1f, fill = false),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = NoFontPaddingTextStyle
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Neutral capsule score pill (softWrap = false)
          Box(
            modifier = Modifier
              .height(30.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(LightSurfaceSubtle)
              .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "운전점수 ",
                fontSize = 11.sp,
                color = TextSecondary,
                softWrap = false,
                maxLines = 1,
                style = NoFontPaddingTextStyle
              )
              Text(
                text = "${state.currentTripScore}점",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.currentTripScore >= 80) EmeraldSafe else CrimsonAlert,
                fontFamily = FontFamily.Monospace,
                softWrap = false,
                maxLines = 1,
                style = NoFontPaddingTextStyle
              )
            }
          }

          // Beep-only Mode Toggle Pill (Single Action, No Duplicate Mute Button)
          Box(
            modifier = Modifier
              .height(30.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (state.isBeepOnlyCoachMode) CyanGlow else LightSurfaceSubtle)
              .border(
                1.dp,
                if (state.isBeepOnlyCoachMode) CyanNeon.copy(alpha = 0.5f) else LightCardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { onToggleBeepMode() }
              .padding(horizontal = 8.dp)
              .testTag("beep_mode_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "비프음 간소화",
                tint = if (state.isBeepOnlyCoachMode) CyanNeon else TextMuted,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (state.isBeepOnlyCoachMode) "비프음 모드" else "음성 안내",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.isBeepOnlyCoachMode) CyanNeon else TextSecondary,
                softWrap = false,
                maxLines = 1,
                style = NoFontPaddingTextStyle
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 2행: 메타 상태 인디케이터
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(if (state.isDriving) EmeraldSafe else TextMuted)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (state.isDriving) "실시간 안전 코칭 중" else "출발 준비 완료",
          fontSize = 11.sp,
          color = if (state.isDriving) EmeraldSafe else TextSecondary,
          fontWeight = FontWeight.Medium,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}
