package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ParkingGracePeriodState
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
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
fun ParkingGraceTimerCard(
  graceState: ParkingGracePeriodState,
  onMoveVehicleComplete: () -> Unit,
  onResetTimer: () -> Unit,
  onSimulate5MinWarning: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isUrgent = graceState.remainingSeconds <= 300 || graceState.isExpired
  val containerBg = if (graceState.isExpired) CrimsonGlow.copy(alpha = 0.5f) else if (isUrgent) AmberGlow.copy(alpha = 0.35f) else LightSurface
  val borderStrokeColor = if (graceState.isExpired) CrimsonAlert else if (isUrgent) AmberWarning.copy(alpha = 0.6f) else LightCardBorder

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = containerBg,
    shadowElevation = 2.dp,
    border = BorderStroke(1.dp, borderStrokeColor)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1행: 가변 타이틀 ── 고정 카운트다운 뱃지 (Anti-Squishing)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(if (isUrgent) CrimsonGlow else CyanGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isUrgent) Icons.Default.Warning else Icons.Default.Alarm,
              contentDescription = null,
              tint = if (isUrgent) CrimsonAlert else CyanNeon,
              modifier = Modifier.size(15.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "주정차 단속 유예 알림",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUrgent) CrimsonAlert else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 카운트다운 시간 뱃지 (Pill, softWrap = false)
        Box(
          modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (graceState.isExpired) CrimsonAlert else if (isUrgent) AmberWarning else CyanNeon)
            .padding(horizontal = 9.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (graceState.isExpired) "단속 확정" else "${graceState.remainingFormatted} 남음",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.White,
            softWrap = false,
            maxLines = 1,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2행: 메타 정보 & 점심시간 유예 여부 태그
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = graceState.cameraName,
          fontSize = 12.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.weight(1f, fill = false),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
        if (graceState.isLunchTimeGrace) {
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(EmeraldGlow)
              .border(1.dp, EmeraldSafe.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "점심 유예 (11:30~14:00)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldSafe,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress bar
      LinearProgressIndicator(
        progress = { graceState.progressPercent },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (graceState.isExpired) CrimsonAlert else if (isUrgent) AmberWarning else CyanNeon,
        trackColor = LightCardBorder
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Notice message
      val noticeMsg = when {
        graceState.isExpired -> "단속 시간이 지났어요. 과태료가 나오기 전에 지금 바로 이동하세요!"
        isUrgent -> "단속까지 5분 남았어요. 과태료가 나오기 전에 차를 이동해 주세요!"
        graceState.isLunchTimeGrace -> "지금은 점심시간 유예 중이에요. 편안하게 식사하고 오세요."
        else -> "주정차 단속 유예시간을 계산하고 있어요. 5분 전에 미리 알려드릴게요."
      }

      Text(
        text = noticeMsg,
        fontSize = 11.sp,
        fontWeight = if (isUrgent) FontWeight.Bold else FontWeight.Normal,
        color = if (graceState.isExpired) CrimsonAlert else if (isUrgent) AmberWarning else TextMuted,
        lineHeight = 16.sp,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons (Streamlined Toss/Apple Style)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onMoveVehicleComplete,
          modifier = Modifier
            .weight(1f)
            .height(40.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = CyanNeon,
            contentColor = Color.White
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "차를 안전한 곳으로 이동했어요",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        IconButton(
          onClick = onResetTimer,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(10.dp))
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "타이머 재시작",
            modifier = Modifier.size(16.dp),
            tint = TextSecondary
          )
        }
      }
    }
  }
}
