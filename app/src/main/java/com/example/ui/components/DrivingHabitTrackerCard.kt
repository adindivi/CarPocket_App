package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioCoachManager
import com.example.model.DrivingHabitMetric
import com.example.model.DrivingHabitTrackerData
import com.example.model.HabitCategory
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
fun DrivingHabitTrackerCard(
  habitData: DrivingHabitTrackerData,
  lowHabitAlertMessage: String? = null,
  onMetricClick: (DrivingHabitMetric) -> Unit = {},
  onTriggerLowBrakingAlert: (() -> Unit)? = null,
  onTriggerLowAccelAlert: (() -> Unit)? = null,
  onReplayLowScoreAlert: (() -> Unit)? = null,
  onResetClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val metrics = remember(habitData) { habitData.toMetricList() }
  var selectedCategory by remember { mutableStateOf<HabitCategory?>(null) }

  val isBrakingLow = habitData.smoothBrakingScore < AudioCoachManager.LOW_HABIT_SCORE_THRESHOLD
  val isAccelLow = habitData.smoothAccelerationScore < AudioCoachManager.LOW_HABIT_SCORE_THRESHOLD
  val hasLowScoreAlert = isBrakingLow || isAccelLow

  val avgScore = habitData.averageScore
  val avgColor = when {
    avgScore >= 85 -> EmeraldSafe
    avgScore >= 75 -> AmberWarning
    else -> CrimsonAlert
  }
  val avgBg = when {
    avgScore >= 85 -> EmeraldGlow
    avgScore >= 75 -> AmberGlow
    else -> CrimsonGlow
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("driving_habit_tracker_card"),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Card Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(if (hasLowScoreAlert) CrimsonGlow else CyanGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (hasLowScoreAlert) Icons.Default.Warning else Icons.Default.Analytics,
              contentDescription = "운전습관 트래커",
              tint = if (hasLowScoreAlert) CrimsonAlert else CyanNeon,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "내 운전습관",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "멈추기 · 출발하기 · 깜빡이 켜기",
              fontSize = 11.sp,
              color = TextMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (onResetClick != null && (hasLowScoreAlert || habitData.suddenBrakeCount > 0 || habitData.rapidAccelCount > 0 || habitData.missedIndicatorCount > 0)) {
            OutlinedButton(
              onClick = onResetClick,
              modifier = Modifier
                .height(30.dp)
                .testTag("reset_habit_scores_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = LightSurfaceSubtle,
                contentColor = TextSecondary
              ),
              border = BorderStroke(1.dp, LightCardBorder),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = "다시 측정", modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                "다시 측정",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(avgBg)
              .border(1.dp, avgColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
              .padding(horizontal = 9.dp, vertical = 4.dp)
          ) {
            Text(
              text = "평균 ${avgScore}점",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = avgColor,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }

      // Active Low-Score TTS Audio Alert Banner (when braking or acceleration drops < 80)
      AnimatedVisibility(visible = hasLowScoreAlert) {
        Column {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(CrimsonGlow)
              .border(1.dp, CrimsonAlert.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
              .clickable { onReplayLowScoreAlert?.invoke() }
              .padding(12.dp)
              .testTag("low_habit_score_tts_alert_banner"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "TTS 저점수 음성 경고",
                tint = CrimsonAlert,
                modifier = Modifier.size(15.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "저점수 음성 코칭 발령",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = CrimsonAlert,
                  style = TextStyle(
                    platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "터치 시 재안내",
                  fontSize = 10.sp,
                  color = TextSecondary,
                  fontWeight = FontWeight.Medium,
                  style = TextStyle(
                    platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                  )
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = lowHabitAlertMessage
                  ?: if (isBrakingLow && isAccelLow) {
                    "제동(${habitData.smoothBrakingScore}점)·가속(${habitData.smoothAccelerationScore}점) 모두 안전 기준(80점) 미만입니다."
                  } else if (isBrakingLow) {
                    "부드러운 제동 점수가 ${habitData.smoothBrakingScore}점으로 안전 기준(80점) 미만입니다."
                  } else {
                    "부드러운 가속 점수가 ${habitData.smoothAccelerationScore}점으로 안전 기준(80점) 미만입니다."
                  },
                fontSize = 11.sp,
                color = TextPrimary,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3 Habit Progress Bar Items
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        metrics.forEach { metric ->
          val isExpanded = selectedCategory == metric.category
          DrivingHabitMetricRow(
            metric = metric,
            isExpanded = isExpanded,
            onClick = {
              selectedCategory = if (isExpanded) null else metric.category
              onMetricClick(metric)
            }
          )
        }
      }

      // Low-Score TTS Alert Simulation / Verification Controls
      if (onTriggerLowBrakingAlert != null || onTriggerLowAccelAlert != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (onTriggerLowBrakingAlert != null) {
            OutlinedButton(
              onClick = onTriggerLowBrakingAlert,
              modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .testTag("test_low_braking_tts_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CrimsonGlow.copy(alpha = 0.55f),
                contentColor = CrimsonAlert
              ),
              border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.35f)),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "급제동 경고 듣기",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }

          if (onTriggerLowAccelAlert != null) {
            OutlinedButton(
              onClick = onTriggerLowAccelAlert,
              modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .testTag("test_low_accel_tts_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AmberGlow.copy(alpha = 0.65f),
                contentColor = AmberWarning
              ),
              border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.35f)),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "급출발 경고 듣기",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Legend Footer for 80-point UBI Insurance Discount Threshold
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(LightSurfaceSubtle)
          .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "80점 아래로 내려가면 음성으로 바로 알려드려요",
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "보험 할인 기준 80점",
          fontSize = 10.sp,
          color = CyanNeon,
          fontWeight = FontWeight.Bold,
          softWrap = false,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }
    }
  }
}

@Composable
fun DrivingHabitMetricRow(
  metric: DrivingHabitMetric,
  isExpanded: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = (metric.score / 100f).coerceIn(0f, 1f),
    animationSpec = spring(stiffness = 180f),
    label = "habitProgress_${metric.category.name}"
  )

  val isLowScore = metric.score < metric.targetScore

  val (categoryLabel: String, primaryColor: Color, softBg: Color, rowTag: String, barTag: String) = when (metric.category) {
    HabitCategory.SMOOTH_BRAKING -> Quintuple(
      "멈춤",
      if (metric.score >= 80) EmeraldSafe else if (metric.score >= 70) AmberWarning else CrimsonAlert,
      if (metric.score >= 80) EmeraldGlow else if (metric.score >= 70) AmberGlow else CrimsonGlow,
      "habit_row_smooth_braking",
      "progress_smooth_braking"
    )
    HabitCategory.SMOOTH_ACCELERATION -> Quintuple(
      "출발",
      if (metric.score >= 80) CyanNeon else if (metric.score >= 70) AmberWarning else CrimsonAlert,
      if (metric.score >= 80) CyanGlow else if (metric.score >= 70) AmberGlow else CrimsonGlow,
      "habit_row_smooth_acceleration",
      "progress_smooth_acceleration"
    )
    HabitCategory.INDICATOR_USAGE -> Quintuple(
      "신호",
      if (metric.score >= 80) EmeraldSafe else if (metric.score >= 70) AmberWarning else CrimsonAlert,
      if (metric.score >= 80) EmeraldGlow else if (metric.score >= 70) AmberGlow else CrimsonGlow,
      "habit_row_indicator_usage",
      "progress_indicator_usage"
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(if (isLowScore || isExpanded) softBg.copy(alpha = 0.35f) else LightSurfaceSubtle.copy(alpha = 0.6f))
      .border(
        width = 1.dp,
        color = if (isLowScore || isExpanded) primaryColor.copy(alpha = 0.4f) else LightCardBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(onClick = onClick)
      .padding(12.dp)
      .testTag(rowTag)
  ) {
    // Top Row: Category Pill + Title + Score Badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f, fill = false)
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(softBg),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = categoryLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            softWrap = false,
            maxLines = 1,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = metric.titleKo,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (isLowScore) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(CrimsonAlert)
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = "주의",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  softWrap = false,
                  maxLines = 1,
                  style = TextStyle(
                    platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                  )
                )
              }
            } else {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(EmeraldGlow)
                  .border(1.dp, EmeraldSafe.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = "안전",
                  fontSize = 9.sp,
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
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = metric.statusComment,
            fontSize = 11.sp,
            color = if (isLowScore) CrimsonAlert else if (metric.violationCount > 0) AmberWarning else TextSecondary,
            fontWeight = if (isLowScore || metric.violationCount > 0) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Score & Weekly Delta Column
      Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "${metric.score}",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = primaryColor,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
          Text(
            text = " / 100",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 2.dp),
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isLowScore) CrimsonGlow else if (metric.weeklyDelta >= 0) EmeraldGlow else AmberGlow)
            .border(
              0.5.dp,
              if (isLowScore) CrimsonAlert.copy(alpha = 0.3f) else if (metric.weeklyDelta >= 0) EmeraldSafe.copy(alpha = 0.3f) else AmberWarning.copy(alpha = 0.3f),
              RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
          Text(
            text = if (isLowScore) "80점 미만" else if (metric.weeklyDelta >= 0) "지난주 +${metric.weeklyDelta}점" else "지난주 ${metric.weeklyDelta}점",
            fontSize = 9.sp,
            color = if (isLowScore) CrimsonAlert else if (metric.weeklyDelta >= 0) EmeraldSafe else AmberWarning,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            maxLines = 1,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Custom Animated Progress Bar with 80-point target threshold marker
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(9.dp)
        .clip(RoundedCornerShape(5.dp))
        .testTag(barTag)
    ) {
      val w = size.width
      val h = size.height

      // Background Track
      drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
      )

      // Filled Progress Bar
      val fillWidth = (w * animatedProgress).coerceAtLeast(6.dp.toPx())
      drawRoundRect(
        brush = Brush.horizontalGradient(
          colors = listOf(primaryColor.copy(alpha = 0.8f), primaryColor)
        ),
        topLeft = Offset.Zero,
        size = Size(fillWidth, h),
        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
      )

      // 80-point Target Threshold Marker
      val targetX = w * (metric.targetScore / 100f)
      drawLine(
        color = Color(0xFF0F172A).copy(alpha = 0.45f),
        start = Offset(targetX, 0f),
        end = Offset(targetX, h),
        strokeWidth = 2.dp.toPx()
      )
    }

    // Expandable AI Coaching Tip
    AnimatedVisibility(visible = isExpanded) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(LightSurface)
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = "음성 코칭",
          tint = primaryColor,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = metric.coachingTip,
          fontSize = 11.sp,
          color = TextPrimary,
          lineHeight = 15.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

private data class Quintuple<A, B, C, D, E>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E
)
