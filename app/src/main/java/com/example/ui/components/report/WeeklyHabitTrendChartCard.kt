package com.example.ui.components.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyHabitRecord
import com.example.model.WeeklyDrivingReportSummary
import com.example.model.WeeklyReportTrendTab
import com.example.ui.theme.AmberWarning
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

@Composable
fun WeeklyHabitTrendChartCard(
  summary: WeeklyDrivingReportSummary,
  selectedTab: WeeklyReportTrendTab,
  onSelectTab: (WeeklyReportTrendTab) -> Unit,
  selectedDayIndex: Int,
  onSelectDayIndex: (Int) -> Unit
) {
  val records = summary.records
  val safeSelectedIndex = selectedDayIndex.coerceIn(0, (records.size - 1).coerceAtLeast(0))
  val selectedRecord = records.getOrNull(safeSelectedIndex)

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("weekly_trend_chart_card"),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "최근 7일 운전 점수",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            style = NoFontPaddingTextStyle
          )
          Text(
            text = "날짜를 누르면 하루 기록을 자세히 볼 수 있어요",
            fontSize = 11.sp,
            color = TextMuted,
            style = NoFontPaddingTextStyle
          )
        }

        val currentTabAvg = when (selectedTab) {
          WeeklyReportTrendTab.OVERALL -> summary.overallSummaryScore
          WeeklyReportTrendTab.SMOOTH_BRAKING -> summary.avgSmoothBraking
          WeeklyReportTrendTab.SMOOTH_ACCELERATION -> summary.avgSmoothAcceleration
          WeeklyReportTrendTab.INDICATOR_USAGE -> summary.avgIndicatorUsage
          WeeklyReportTrendTab.ALL_COMPARISON -> summary.overallSummaryScore
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyanGlow)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "7일 평균 ${currentTabAvg}점",
            fontSize = 11.sp,
            color = CyanNeon,
            fontWeight = FontWeight.Bold,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4-Tab Segmented Control (Anti-Squishing & Zero Scroll)
      val tabs = listOf(
        WeeklyReportTrendTab.OVERALL to "종합",
        WeeklyReportTrendTab.SMOOTH_BRAKING to "멈춤",
        WeeklyReportTrendTab.SMOOTH_ACCELERATION to "출발",
        WeeklyReportTrendTab.INDICATOR_USAGE to "깜빡이"
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(LightSurfaceSubtle)
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        tabs.forEach { (tab, label) ->
          val isSelected = selectedTab == tab
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) LightSurface else Color.Transparent)
              .border(
                1.dp,
                if (isSelected) LightCardBorder else Color.Transparent,
                RoundedCornerShape(8.dp)
              )
              .clickable { onSelectTab(tab) }
              .padding(vertical = 6.dp)
              .testTag("trend_tab_${tab.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) CyanNeon else TextSecondary,
              softWrap = false,
              maxLines = 1,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 7-Day Trend Canvas (Bars + Trend Line or 3-Series Comparison Lines)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val count = records.size.coerceAtLeast(1)
          val slotWidth = w / count
          val barWidth = slotWidth * 0.42f

          // 80-point UBI Insurance Discount Threshold Dashed Line
          val thresholdY = h * (1f - (80f / 100f))
          drawLine(
            color = Color(0xFFD97706).copy(alpha = 0.55f),
            start = Offset(0f, thresholdY),
            end = Offset(w, thresholdY),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
          )

          if (selectedTab == WeeklyReportTrendTab.ALL_COMPARISON) {
            val series = listOf(
              Pair(EmeraldSafe) { r: DailyHabitRecord -> r.smoothBrakingScore },
              Pair(CyanNeon) { r: DailyHabitRecord -> r.smoothAccelerationScore },
              Pair(AmberWarning) { r: DailyHabitRecord -> r.indicatorUsageScore }
            )

            series.forEach { (lineColor, extractor) ->
              val path = Path()
              records.forEachIndexed { index, rec ->
                val score = extractor(rec).coerceIn(0, 100)
                val cx = index * slotWidth + slotWidth / 2f
                val cy = h * (1f - (score / 100f))
                if (index == 0) path.moveTo(cx, cy) else path.lineTo(cx, cy)
              }
              drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
              )

              records.forEachIndexed { index, rec ->
                val score = extractor(rec).coerceIn(0, 100)
                val cx = index * slotWidth + slotWidth / 2f
                val cy = h * (1f - (score / 100f))
                drawCircle(
                  color = lineColor,
                  radius = if (index == safeSelectedIndex) 5.dp.toPx() else 3.5.dp.toPx(),
                  center = Offset(cx, cy)
                )
              }
            }
          } else {
            val scoreSelector: (DailyHabitRecord) -> Int = when (selectedTab) {
              WeeklyReportTrendTab.OVERALL -> { r -> r.summaryScore }
              WeeklyReportTrendTab.SMOOTH_BRAKING -> { r -> r.smoothBrakingScore }
              WeeklyReportTrendTab.SMOOTH_ACCELERATION -> { r -> r.smoothAccelerationScore }
              WeeklyReportTrendTab.INDICATOR_USAGE -> { r -> r.indicatorUsageScore }
              WeeklyReportTrendTab.ALL_COMPARISON -> { r -> r.summaryScore }
            }

            val trendPath = Path()

            records.forEachIndexed { index, record ->
              val score = scoreSelector(record).coerceIn(0, 100)
              val barHeight = h * (score / 100f)
              val x = index * slotWidth + (slotWidth - barWidth) / 2f
              val y = h - barHeight
              val centerX = index * slotWidth + slotWidth / 2f

              val isDaySelected = index == safeSelectedIndex
              val baseColor = when {
                score >= 90 -> EmeraldSafe
                score >= 80 -> CyanNeon
                else -> AmberWarning
              }

              drawRoundRect(
                color = if (isDaySelected) baseColor else baseColor.copy(alpha = 0.55f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
              )

              val lineY = (y + 4.dp.toPx()).coerceIn(6.dp.toPx(), h - 6.dp.toPx())
              if (index == 0) trendPath.moveTo(centerX, lineY) else trendPath.lineTo(centerX, lineY)
            }

            // Overlay connecting trend curve
            drawPath(
              path = trendPath,
              color = TextPrimary.copy(alpha = 0.35f),
              style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
          }
        }
      }

      if (selectedTab == WeeklyReportTrendTab.ALL_COMPARISON) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          LegendDot(color = EmeraldSafe, label = "부드럽게 멈춤")
          Spacer(modifier = Modifier.width(12.dp))
          LegendDot(color = CyanNeon, label = "부드럽게 출발")
          Spacer(modifier = Modifier.width(12.dp))
          LegendDot(color = AmberWarning, label = "깜빡이 켜기")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Interactive Day Selector Row (월 ~ 일)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        records.forEachIndexed { index, record ->
          val isSelected = index == safeSelectedIndex
          val displayScore = when (selectedTab) {
            WeeklyReportTrendTab.OVERALL -> record.summaryScore
            WeeklyReportTrendTab.SMOOTH_BRAKING -> record.smoothBrakingScore
            WeeklyReportTrendTab.SMOOTH_ACCELERATION -> record.smoothAccelerationScore
            WeeklyReportTrendTab.INDICATOR_USAGE -> record.indicatorUsageScore
            WeeklyReportTrendTab.ALL_COMPARISON -> record.summaryScore
          }

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) CyanGlow else Color.Transparent)
              .clickable { onSelectDayIndex(index) }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("day_selector_${record.dayOfWeek}")
          ) {
            Text(
              text = "$displayScore",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) CyanNeon else TextSecondary,
              fontFamily = FontFamily.Monospace,
              style = NoFontPaddingTextStyle
            )
            Text(
              text = if (record.isToday) "${record.dayOfWeek}(오늘)" else record.dayOfWeek,
              fontSize = 11.sp,
              fontWeight = if (isSelected || record.isToday) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) CyanNeon else TextMuted,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }

      // Selected Day Detailed Habit Breakdown Box
      if (selectedRecord != null) {
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = LightCardBorder)
        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurfaceSubtle)
            .padding(12.dp)
            .testTag("selected_day_detail_box")
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${selectedRecord.dateLabel} 운전 기록",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                style = NoFontPaddingTextStyle
              )
              Text(
                text = "종합 ${selectedRecord.summaryScore}점 · ${selectedRecord.distanceKm}km",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyanNeon,
                fontFamily = FontFamily.Monospace,
                style = NoFontPaddingTextStyle
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              DayHabitMiniPill(
                label = "부드럽게 멈춤",
                score = selectedRecord.smoothBrakingScore,
                sub = "급제동 ${selectedRecord.suddenBrakeCount}회",
                modifier = Modifier.weight(1f)
              )
              DayHabitMiniPill(
                label = "부드럽게 출발",
                score = selectedRecord.smoothAccelerationScore,
                sub = "급출발 ${selectedRecord.rapidAccelCount}회",
                modifier = Modifier.weight(1f)
              )
              DayHabitMiniPill(
                label = "깜빡이 켜기",
                score = selectedRecord.indicatorUsageScore,
                sub = "깜빡이 안 켬 ${selectedRecord.missedIndicatorCount}회",
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }
  }
}
