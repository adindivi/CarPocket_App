package com.example.ui.components.cockpit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.util.NoFontPaddingTextStyle
import com.example.util.toFormattedKm
import com.example.util.toFormattedPoints
import com.example.viewmodel.CockpitUiState

@Composable
fun LiveDriveMetricsRow(state: CockpitUiState) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    MetricTile(
      label = "달린 거리",
      value = state.tripDistanceKm.toFormattedKm(),
      valueColor = TextPrimary,
      modifier = Modifier.weight(1f)
    )

    val mins = state.tripDurationSeconds / 60
    val secs = state.tripDurationSeconds % 60
    MetricTile(
      label = "운전 시간",
      value = String.format("%02d:%02d", mins, secs),
      valueColor = TextPrimary,
      modifier = Modifier.weight(1f)
    )

    MetricTile(
      label = "쌓인 포인트",
      value = state.accumulatedPoints.toFormattedPoints(),
      valueColor = CyanNeon,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
fun MetricTile(
  label: String,
  value: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
      Text(
        text = label,
        fontSize = 11.sp,
        color = TextMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = NoFontPaddingTextStyle
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = valueColor,
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = NoFontPaddingTextStyle
      )
    }
  }
}
