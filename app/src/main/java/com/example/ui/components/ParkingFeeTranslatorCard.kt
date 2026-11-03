package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.model.ParkingFeeData
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
import com.example.util.NoFontPaddingTextStyle

@Composable
fun ParkingFeeTranslatorCard(
  feeData: ParkingFeeData,
  onSelectHours: (Int) -> Unit,
  onSpeakBriefing: () -> Unit,
  modifier: Modifier = Modifier
) {
  val hourOptions = listOf(
    1 to "1시간",
    2 to "2시간",
    4 to "4시간",
    8 to "종일(8h)"
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1행: 가변 타이틀 ── 무료 회차 뱃지 (Anti-Squishing)
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
              .size(28.dp)
              .clip(CircleShape)
              .background(CyanGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
              contentDescription = null,
              tint = CyanNeon,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "주차 요금 1초 계산기",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(EmeraldGlow)
            .border(1.dp, EmeraldSafe.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "무료 회차 ${feeData.freeGraceMinutes}분",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldSafe,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2행: OCR 카메라 스캔 표지판 결과
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LightSurfaceSubtle,
        border = BorderStroke(1.dp, LightCardBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = feeData.parkingLotName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "${feeData.baseRateText} · ${feeData.extraRateText} · 일 최대 ${String.format("%,d", feeData.maxDailyRateWon)}원",
            fontSize = 11.sp,
            color = TextSecondary,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3행: 내 예상 주차 시간 선택 칩
      Text(
        text = "내 예상 주차 시간 선택",
        fontSize = 11.sp,
        color = TextMuted,
        fontWeight = FontWeight.Medium,
        style = NoFontPaddingTextStyle
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        hourOptions.forEach { (h, label) ->
          val isSelected = (feeData.selectedHours == h)
          Box(
            modifier = Modifier
              .weight(1f)
              .height(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) CyanNeon else LightSurfaceSubtle)
              .border(1.dp, if (isSelected) CyanNeon else LightCardBorder, RoundedCornerShape(8.dp))
              .clickable { onSelectHours(h) },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else TextPrimary,
              softWrap = false,
              maxLines = 1,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4행: 실시간 요금 계산 결과 하이라이트 박스
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CyanGlow.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.35f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              text = "${feeData.selectedHours}시간 예상 요금",
              fontSize = 11.sp,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${String.format("%,d", feeData.calculatedFeeWon)}원",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = CyanNeon,
              maxLines = 1,
              softWrap = false,
              style = NoFontPaddingTextStyle
            )
          }

          Button(
            onClick = onSpeakBriefing,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "소리로 듣기",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              maxLines = 1,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }
    }
  }
}
