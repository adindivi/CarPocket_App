package com.example.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyHabitRecord
import com.example.model.DriveSession
import com.example.util.toCurrencyWon
import com.example.model.WeeklyDrivingReportSummary
import com.example.model.WeeklyReportTrendTab
import com.example.ui.components.DrivingHabitTrackerCard
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldStar
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import com.example.viewmodel.SafeCoachViewModel

@Composable
fun WeeklyDrivingReportScreen(
  viewModel: SafeCoachViewModel,
  modifier: Modifier = Modifier
) {
  ReportScreen(viewModel = viewModel, modifier = modifier)
}

@Composable
fun ReportScreen(
  viewModel: SafeCoachViewModel,
  modifier: Modifier = Modifier
) {
  val summary by viewModel.weeklyReportSummary.collectAsState()
  val recentTrips by viewModel.recentTrips.collectAsState()
  val cockpitState by viewModel.cockpitState.collectAsState()

  var selectedTrendTab by remember { mutableStateOf(WeeklyReportTrendTab.OVERALL) }
  var selectedDayIndex by remember(summary.records.size) {
    mutableIntStateOf((summary.records.size - 1).coerceAtLeast(0))
  }

  val animatedSummaryScore by animateIntAsState(
    targetValue = summary.overallSummaryScore,
    animationSpec = spring(stiffness = 180f),
    label = "weeklySummaryScore"
  )

  val summaryColor = when {
    summary.overallSummaryScore >= 85 -> EmeraldSafe
    summary.overallSummaryScore >= 75 -> AmberWarning
    else -> CrimsonAlert
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(LightAppBg),
    contentAlignment = Alignment.TopCenter
  ) {
    val isWideLayout = maxWidth >= 600.dp
    val horizontalPad = if (maxWidth < 380.dp) 12.dp else 16.dp

    if (isWideLayout) {
      // Landscape / Fold / Tablet Responsive 2-Pane Layout
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = horizontalPad),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Left Column: Weekly Master Score Card + Highlights + AI Coaching
        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            WeeklyMasterReportCard(
              summary = summary,
              animatedSummaryScore = animatedSummaryScore,
              summaryColor = summaryColor,
              onSpeak = { viewModel.speakWeeklyReportSummary() },
              onSyncToday = { viewModel.syncTodayHabitToWeeklyReport() }
            )
          }
          item { BestAndLowestDayRow(summary = summary) }
          item { WeeklyAiCoachingCards(summary = summary) }
        }

        // Right Column: 7-Day Trend Chart + Driving Habit Tracker + Recent Sessions
        LazyColumn(
          modifier = Modifier.weight(1.15f),
          contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            WeeklyHabitTrendChartCard(
              summary = summary,
              selectedTab = selectedTrendTab,
              onSelectTab = { selectedTrendTab = it },
              selectedDayIndex = selectedDayIndex,
              onSelectDayIndex = { selectedDayIndex = it }
            )
          }
          item {
            DrivingHabitTrackerCard(
              habitData = cockpitState.habitTracker,
              lowHabitAlertMessage = cockpitState.lowHabitAlertMessage,
              onMetricClick = { viewModel.speakHabitCoachingTip(it) },
              onTriggerLowBrakingAlert = { viewModel.triggerLowBrakingHabitAlert() },
              onTriggerLowAccelAlert = { viewModel.triggerLowAccelerationHabitAlert() },
              onReplayLowScoreAlert = { viewModel.replayLowHabitScoreAlert() },
              onResetClick = { viewModel.resetHabitScores() }
            )
          }
          item {
            Text(
              text = "최근 운전 기록",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              modifier = Modifier.padding(top = 4.dp),
              style = NoFontPaddingTextStyle
            )
          }
          items(recentTrips) { trip ->
            TripHistoryItem(trip = trip)
          }
        }
      }
    } else {
      // Portrait Responsive Layout (Galaxy S24, 360-412dp)
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 640.dp)
          .padding(horizontal = horizontalPad),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          WeeklyMasterReportCard(
            summary = summary,
            animatedSummaryScore = animatedSummaryScore,
            summaryColor = summaryColor,
            onSpeak = { viewModel.speakWeeklyReportSummary() },
            onSyncToday = { viewModel.syncTodayHabitToWeeklyReport() }
          )
        }
        item {
          WeeklyHabitTrendChartCard(
            summary = summary,
            selectedTab = selectedTrendTab,
            onSelectTab = { selectedTrendTab = it },
            selectedDayIndex = selectedDayIndex,
            onSelectDayIndex = { selectedDayIndex = it }
          )
        }
        item { BestAndLowestDayRow(summary = summary) }
        item {
          DrivingHabitTrackerCard(
            habitData = cockpitState.habitTracker,
            lowHabitAlertMessage = cockpitState.lowHabitAlertMessage,
            onMetricClick = { viewModel.speakHabitCoachingTip(it) },
            onTriggerLowBrakingAlert = { viewModel.triggerLowBrakingHabitAlert() },
            onTriggerLowAccelAlert = { viewModel.triggerLowAccelerationHabitAlert() },
            onReplayLowScoreAlert = { viewModel.replayLowHabitScoreAlert() },
            onResetClick = { viewModel.resetHabitScores() }
          )
        }
        item { WeeklyAiCoachingCards(summary = summary) }
        item {
          Text(
            text = "최근 운전 기록",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 4.dp),
            style = NoFontPaddingTextStyle
          )
        }
        items(recentTrips) { trip ->
          TripHistoryItem(trip = trip)
        }
      }
    }
  }
}

@Composable
private fun WeeklyMasterReportCard(
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

@Composable
private fun WeeklyAiCoachingCards(summary: WeeklyDrivingReportSummary) {
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

@Composable
private fun WeeklyHabitTrendChartCard(
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
            color = TextPrimary
          )
          Text(
            text = "날짜를 누르면 하루 기록을 자세히 볼 수 있어요",
            fontSize = 11.sp,
            color = TextMuted
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
            fontWeight = FontWeight.Bold
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
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
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
            // Draw 3 comparison trend lines (Braking = Emerald, Accel = Cyan, Indicator = Amber)
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
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = if (record.isToday) "${record.dayOfWeek}(오늘)" else record.dayOfWeek,
              fontSize = 11.sp,
              fontWeight = if (isSelected || record.isToday) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) CyanNeon else TextMuted
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
                color = TextPrimary
              )
              Text(
                text = "종합 ${selectedRecord.summaryScore}점 · ${selectedRecord.distanceKm}km",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyanNeon,
                fontFamily = FontFamily.Monospace
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

@Composable
private fun LegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun DayHabitMiniPill(
  label: String,
  score: Int,
  sub: String,
  modifier: Modifier = Modifier
) {
  val pillColor = when {
    score >= 85 -> EmeraldSafe
    score >= 75 -> AmberWarning
    else -> CrimsonAlert
  }
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(LightSurface)
      .border(1.dp, LightCardBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 8.dp, vertical = 7.dp)
  ) {
    Column {
      Text(label, fontSize = 10.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
      Text(
        text = "${score}점",
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = pillColor,
        fontFamily = FontFamily.Monospace
      )
      Text(sub, fontSize = 9.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
  }
}

@Composable
private fun BestAndLowestDayRow(summary: WeeklyDrivingReportSummary) {
  val best = summary.bestDayRecord
  val lowest = summary.lowestDayRecord
  if (best == null || lowest == null) return

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Surface(
      modifier = Modifier.weight(1f),
      shape = RoundedCornerShape(14.dp),
      color = LightSurface,
      shadowElevation = 1.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(EmeraldGlow)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              "BEST",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldSafe,
              style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "가장 안전했던 날",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldSafe,
            style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          best.dateLabel,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "종합 ${best.summaryScore}점 · ${best.distanceKm}km 달림",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = EmeraldSafe,
          fontFamily = FontFamily.Monospace,
          style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
        )
      }
    }

    Surface(
      modifier = Modifier.weight(1f),
      shape = RoundedCornerShape(14.dp),
      color = LightSurface,
      shadowElevation = 1.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(AmberGlow)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              "FOCUS",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = AmberWarning,
              style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "조금 아쉬웠던 날",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AmberWarning,
            style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          lowest.dateLabel,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "종합 ${lowest.summaryScore}점 · 멈춤 ${lowest.smoothBrakingScore}점",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = AmberWarning,
          fontFamily = FontFamily.Monospace,
          style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
        )
      }
    }
  }
}

@Composable
fun MetricScoreBar(
  label: String,
  score: Int,
  color: Color,
  delta: Int? = null
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      color = TextSecondary,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f, fill = false),
      style = TextStyle(
        platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
      )
    )
    Spacer(modifier = Modifier.width(12.dp))
    LinearProgressIndicator(
      progress = { (score / 100f).coerceIn(0f, 1f) },
      modifier = Modifier
        .width(100.dp)
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = LightSurfaceSubtle
    )
    Spacer(modifier = Modifier.width(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "${score}점",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        fontFamily = FontFamily.Monospace,
        softWrap = false,
        maxLines = 1,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )
      if (delta != null) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(if (delta >= 0) EmeraldGlow else AmberGlow)
            .border(
              0.5.dp,
              if (delta >= 0) EmeraldSafe.copy(alpha = 0.35f) else AmberWarning.copy(alpha = 0.35f),
              RoundedCornerShape(3.dp)
            )
            .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
          Text(
            text = if (delta >= 0) "+$delta" else "$delta",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (delta >= 0) EmeraldSafe else AmberWarning,
            fontFamily = FontFamily.Monospace,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }
  }
}

@Composable
fun InsightBullet(text: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .padding(top = 6.dp)
        .size(4.5.dp)
        .clip(CircleShape)
        .background(TextSecondary.copy(alpha = 0.7f))
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      fontSize = 12.sp,
      color = TextSecondary,
      lineHeight = 18.sp,
      style = TextStyle(
        platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
      )
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TripHistoryItem(trip: DriveSession) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            trip.dateLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            "${trip.timeLabel} · ${trip.totalKm}km 달림",
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (trip.score >= 90) EmeraldGlow else AmberGlow)
            .border(1.dp, if (trip.score >= 90) EmeraldSafe.copy(alpha = 0.3f) else AmberWarning.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${trip.score}점",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (trip.score >= 90) EmeraldSafe else AmberWarning,
            fontFamily = FontFamily.Monospace,
            softWrap = false,
            style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        TripTag("급출발 ${trip.rapidAccelCount}회")
        TripTag("급제동 ${trip.suddenBrakeCount}회")
        TripTag("깜빡이 ${trip.blinkerCompliancePercent}%")
        TripTag("기름값 ${trip.fuelSavedWon.toCurrencyWon()} 절약")
      }
    }
  }
}

@Composable
fun TripTag(text: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(LightSurfaceSubtle)
      .border(1.dp, LightCardBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      fontSize = 10.sp,
      color = TextSecondary,
      fontWeight = FontWeight.Medium,
      softWrap = false,
      style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
    )
  }
}
