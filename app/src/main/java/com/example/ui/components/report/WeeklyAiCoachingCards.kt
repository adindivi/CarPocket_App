package com.example.ui.components.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.model.WeeklyDrivingReportSummary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.util.NoFontPaddingTextStyle

@Composable
fun WeeklyAiCoachingCards(summary: WeeklyDrivingReportSummary) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // AI Coaching: Well-Done Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      color = LightSurface,
      shadowElevation = 2.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(EmeraldGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "이번 주 칭찬해요",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldSafe,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        InsightBullet(
          text = "최근 7일간 부드럽게 출발하기 평균 ${summary.avgSmoothAcceleration}점으로 안정적인 주행을 이어갔어요."
        )
        InsightBullet(
          text = "주간 7일 중 ${summary.qualifiedDaysCount}일 동안 보험 할인 기준(80점 이상)을 달성했어요."
        )
        InsightBullet(
          text = "차선 바꿀 때 깜빡이 준수율 ${summary.avgIndicatorUsage}%로 배려 넘치는 주행을 했어요."
        )
      }
    }

    // AI Coaching: To Improve Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      color = LightSurface,
      shadowElevation = 2.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(AmberGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "이것만 챙기면 더 좋아요",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AmberWarning,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        InsightBullet(
          text = if (summary.totalSuddenBrakes > 0) {
            "최근 7일간 급제동이 총 ${summary.totalSuddenBrakes}회 있었어요. 신호가 바뀌기 전에 3초 먼저 여유를 두면 좋아요."
          } else {
            "급제동 0회 유지 중이에요! 비 오는 날이나 밤길에도 지금처럼 안전거리를 지켜주세요."
          }
        )
        InsightBullet(
          text = "신호 대기 후 출발할 때 처음 3초간 천천히 밟으면 연비와 승차감을 모두 챙길 수 있어요."
        )
        InsightBullet(
          text = "다리나 터널 앞 실선 구간은 차선 변경 금지구역이니 미리 차선을 바꿔두세요."
        )
      }
    }
  }
}
