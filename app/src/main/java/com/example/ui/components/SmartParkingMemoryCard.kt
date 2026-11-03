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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
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
import com.example.model.ParkingMemoryData
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
fun SmartParkingMemoryCard(
  parkingMemory: ParkingMemoryData,
  onScanPillar: () -> Unit,
  onAskWhereIsMyCar: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1행: 가변 타이틀 ── 고정 위치 뱃지 (Anti-Squishing)
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
              imageVector = Icons.Default.LocalParking,
              contentDescription = null,
              tint = CyanNeon,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "내 차 위치 · 문콕 안심 가이드",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 고정 기둥 번호 뱃지 (softWrap = false)
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
            text = "${parkingMemory.floor} · ${parkingMemory.pillarNumber}",
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

      // 2행: 주차 시간 메타
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(EmeraldSafe)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${parkingMemory.parkingTime} · 카메라가 자동으로 찾았어요",
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium,
          style = NoFontPaddingTextStyle
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3행: 문콕 방어 좌우 여유 공간 타일 (Fintech Neutral Style)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          color = LightSurfaceSubtle,
          border = BorderStroke(1.dp, LightCardBorder)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "운전석 쪽 여유",
              fontSize = 10.sp,
              color = TextMuted,
              style = NoFontPaddingTextStyle
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "${parkingMemory.leftClearanceCm}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = EmeraldSafe,
                style = NoFontPaddingTextStyle
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "cm (안전)",
                fontSize = 11.sp,
                color = EmeraldSafe,
                fontWeight = FontWeight.Bold,
                style = NoFontPaddingTextStyle
              )
            }
          }
        }

        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          color = LightSurfaceSubtle,
          border = BorderStroke(1.dp, LightCardBorder)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "기둥 쪽 여유",
              fontSize = 10.sp,
              color = TextMuted,
              style = NoFontPaddingTextStyle
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "${parkingMemory.rightClearanceCm}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = CyanNeon,
                style = NoFontPaddingTextStyle
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "cm (밀착)",
                fontSize = 11.sp,
                color = CyanNeon,
                fontWeight = FontWeight.Bold,
                style = NoFontPaddingTextStyle
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4행: Gleo AI 문콕 방어 조언 박스
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CyanGlow.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = CyanNeon,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = parkingMemory.defenseAdvice,
            fontSize = 11.sp,
            color = TextPrimary,
            lineHeight = 16.sp,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5행: 단일 액션 버튼 (기둥 번호 다시 촬영 및 스캔)
      Button(
        onClick = onScanPillar,
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = LightSurfaceSubtle,
          contentColor = TextPrimary
        ),
        border = BorderStroke(1.dp, LightCardBorder),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          modifier = Modifier.size(15.dp),
          tint = CyanNeon
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "기둥 번호 다시 찍기",
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
