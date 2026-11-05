package com.example.ui.components.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeeklyDrivingReportSummary
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle

@Composable
fun WeeklyMasterReportCard(
  summary: WeeklyDrivingReportSummary,
  animatedSummaryScore: Int,
  summaryColor: Color,
  onSpeak: () -> Unit,
  onSyncToday: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("report_master_card"),
    shape = RoundedCornerShape(22.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // 1행: 가변 타이틀 ── 고정 수치 트렌드 뱃지 (Anti-Squishing)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            text = "주간 운전 리포트",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = summary.driverTierTitle,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        val isPositiveTrend = summary.overallTrendDelta >= 0
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPositiveTrend) EmeraldGlow else CrimsonGlow)
            .border(
              1.dp,
              if (isPositiveTrend) EmeraldSafe.copy(alpha = 0.4f) else CrimsonAlert.copy(alpha = 0.4f),
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("weekly_trend_delta_badge")
        ) {
          Text(
            text = if (isPositiveTrend) "지난주보다 +${summary.overallTrendDelta}점" else "지난주보다 ${summary.overallTrendDelta}점",
            fontSize = 11.sp,
            color = if (isPositiveTrend) EmeraldSafe else CrimsonAlert,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Main Calculated Summary Score + Savings Summary
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Row(
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.testTag("weekly_summary_score_text")
        ) {
          Text(
            text = "$animatedSummaryScore",
            fontSize = 54.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = summaryColor,
            style = NoFontPaddingTextStyle
          )
          Text(
            text = " / 100점",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp),
            style = NoFontPaddingTextStyle
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "7일간 ${summary.totalDistanceKm}km 주행 · 매일 달성",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "1년에 ${String.format("%,d", summary.estimatedAnnualSavingsWon)}원 절약",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CyanNeon,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 7-Day Average Progress Bars
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricScoreBar(
          label = "부드럽게 멈추기",
          score = summary.avgSmoothBraking,
          delta = summary.brakingTrendDelta,
          color = CyanNeon
        )
        MetricScoreBar(
          label = "부드럽게 출발하기",
          score = summary.avgSmoothAcceleration,
          delta = summary.accelerationTrendDelta,
          color = CyanNeon
        )
        MetricScoreBar(
          label = "깜빡이 켜기",
          score = summary.avgIndicatorUsage,
          delta = summary.indicatorTrendDelta,
          color = CyanNeon
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onSpeak,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("speak_weekly_report_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CyanGlow.copy(alpha = 0.5f),
            contentColor = CyanNeon
          ),
          border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f)),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text("음성으로 요약 듣기", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis, style = NoFontPaddingTextStyle)
        }

        Button(
          onClick = onSyncToday,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("sync_today_habit_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = CyanNeon,
            contentColor = Color.White
          ),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text("오늘 기록 반영하기", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis, style = NoFontPaddingTextStyle)
        }
      }
    }
  }
}
