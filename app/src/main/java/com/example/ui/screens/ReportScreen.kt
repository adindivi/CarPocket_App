package com.example.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeeklyReportTrendTab
import com.example.ui.components.DrivingHabitTrackerCard
import com.example.ui.components.report.BestAndLowestDayRow
import com.example.ui.components.report.TripHistoryItem
import com.example.ui.components.report.WeeklyAiCoachingCards
import com.example.ui.components.report.WeeklyHabitTrendChartCard
import com.example.ui.components.report.WeeklyMasterReportCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.TextPrimary
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
