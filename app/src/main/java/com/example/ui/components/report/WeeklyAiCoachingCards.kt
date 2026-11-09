package com.example.ui.components.report

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeeklyDrivingReportSummary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
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
fun WeeklyAiCoachingCards(summary: WeeklyDrivingReportSummary) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // 1. 핵심 칭찬 카드 (간결하고 즉각적인 2행 인포그래픽)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = LightSurface,
      shadowElevation = 1.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        // 1행: 아이콘 + 핵심 칭찬 제목 ── 고정 수치 뱃지 (Anti-Squishing)
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
                .size(24.dp)
                .clip(CircleShape)
                .background(EmeraldGlow),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldSafe,
                modifier = Modifier.size(14.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "이번 주 칭찬: 부드러운 출발 & 깜빡이 준수",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldSafe,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(EmeraldGlow)
              .border(1.dp, EmeraldSafe.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(
              text = "우수 ${summary.avgSmoothAcceleration}점",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldSafe,
              softWrap = false,
              maxLines = 1,
              style = NoFontPaddingTextStyle
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2행: 핵심 1줄 요약
        Text(
          text = "깜빡이 준수 ${summary.avgIndicatorUsage}% 달성 · ${summary.qualifiedDaysCount}일 연속 보험 할인 통과",
          fontSize = 12.sp,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = NoFontPaddingTextStyle
        )
      }
    }

    // 2. 집중 개선 카드 (장황한 줄글 배제, 핵심 코칭 1줄 집중)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = LightSurface,
      shadowElevation = 1.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        // 1행: 아이콘 + 개선 제목 ── 발생 횟수 뱃지
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
                .size(24.dp)
                .clip(CircleShape)
                .background(AmberGlow),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = AmberWarning,
                modifier = Modifier.size(14.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (summary.totalSuddenBrakes > 0) "신호 앞 정차 시 여유 두기" else "완벽한 안전거리 유지 중",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = AmberWarning,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (summary.totalSuddenBrakes > 0) AmberGlow else LightSurfaceSubtle)
              .border(
                1.dp,
                if (summary.totalSuddenBrakes > 0) AmberWarning.copy(alpha = 0.35f) else LightCardBorder,
                RoundedCornerShape(6.dp)
              )
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(
              text = if (summary.totalSuddenBrakes > 0) "급제동 ${summary.totalSuddenBrakes}회" else "0회 퍼펙트",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (summary.totalSuddenBrakes > 0) AmberWarning else TextMuted,
              softWrap = false,
              maxLines = 1,
              style = NoFontPaddingTextStyle
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2행: 핵심 1줄 실천 가이드
        Text(
          text = if (summary.totalSuddenBrakes > 0) {
            "정차 3초 전 미리 감속하면 승차감과 연비(12%↑) 개선"
          } else {
            "야간·우천 시에도 지금처럼 안전거리 30m 유지 권장"
          },
          fontSize = 12.sp,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}
