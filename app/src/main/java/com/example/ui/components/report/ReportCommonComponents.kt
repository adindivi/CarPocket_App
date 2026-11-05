package com.example.ui.components.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DriveSession
import com.example.model.WeeklyDrivingReportSummary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import com.example.util.toCurrencyWon

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
      style = NoFontPaddingTextStyle
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
        style = NoFontPaddingTextStyle
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
            style = NoFontPaddingTextStyle
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
      style = NoFontPaddingTextStyle
    )
  }
}

@Composable
fun LegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold, style = NoFontPaddingTextStyle)
  }
}

@Composable
fun DayHabitMiniPill(
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
      Text(label, fontSize = 10.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis, style = NoFontPaddingTextStyle)
      Text(
        text = "${score}점",
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = pillColor,
        fontFamily = FontFamily.Monospace,
        style = NoFontPaddingTextStyle
      )
      Text(sub, fontSize = 9.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, style = NoFontPaddingTextStyle)
    }
  }
}

@Composable
fun BestAndLowestDayRow(summary: WeeklyDrivingReportSummary) {
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
              style = NoFontPaddingTextStyle
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "가장 안전했던 날",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldSafe,
            style = NoFontPaddingTextStyle
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          best.dateLabel,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          style = NoFontPaddingTextStyle
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "종합 ${best.summaryScore}점 · ${best.distanceKm}km 달림",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = EmeraldSafe,
          fontFamily = FontFamily.Monospace,
          style = NoFontPaddingTextStyle
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
              style = NoFontPaddingTextStyle
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "조금 아쉬웠던 날",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AmberWarning,
            style = NoFontPaddingTextStyle
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          lowest.dateLabel,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          style = NoFontPaddingTextStyle
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "종합 ${lowest.summaryScore}점 · 멈춤 ${lowest.smoothBrakingScore}점",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = AmberWarning,
          fontFamily = FontFamily.Monospace,
          style = NoFontPaddingTextStyle
        )
      }
    }
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
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            "${trip.timeLabel} · ${trip.totalKm}km 달림",
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
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
            style = NoFontPaddingTextStyle
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
      style = NoFontPaddingTextStyle
    )
  }
}
