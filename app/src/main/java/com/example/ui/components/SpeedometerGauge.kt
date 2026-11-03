package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerGauge(
  speed: Int,
  speedLimit: Int,
  gForce: Float,
  modifier: Modifier = Modifier
) {
  val animatedSpeed by animateFloatAsState(
    targetValue = speed.toFloat(),
    animationSpec = spring(stiffness = 200f),
    label = "speed"
  )

  val isOverSpeed = speed > speedLimit

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(22.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = androidx.compose.foundation.BorderStroke(
      width = if (isOverSpeed) 1.5.dp else 1.dp,
      color = if (isOverSpeed) CrimsonAlert else LightCardBorder
    )
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      // Responsive dial size for Galaxy S24 (compact width ~360-412dp)
      val dialSize = (maxWidth * 0.48f).coerceIn(156.dp, 188.dp)
      val speedFontSize = if (maxWidth < 360.dp) 44.sp else 50.sp

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier.size(dialSize),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(dialSize - 8.dp)) {
            val sweepStart = 140f
            val totalSweep = 260f
            val strokeWidth = 13.dp.toPx()

            // Light Mode Track Background Arc
            drawArc(
              color = Color(0xFFE2E8F0),
              startAngle = sweepStart,
              sweepAngle = totalSweep,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Speed Active Arc (Single Refined Accent - Red on OverSpeed)
            val progressRatio = (animatedSpeed / 140f).coerceIn(0f, 1f)
            val activeSweep = totalSweep * progressRatio

            drawArc(
              color = if (isOverSpeed) CrimsonAlert else CyanNeon,
              startAngle = sweepStart,
              sweepAngle = activeSweep,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Tick Marks for Light Mode
            val numTicks = 9
            for (i in 0..numTicks) {
              val angleDeg = sweepStart + (totalSweep / numTicks) * i
              val rad = angleDeg * PI / 180f
              val rOuter = size.width / 2f - strokeWidth / 2 - 4f
              val rInner = rOuter - 9f
              val startX = (size.width / 2f + rInner * cos(rad)).toFloat()
              val startY = (size.height / 2f + rInner * sin(rad)).toFloat()
              val endX = (size.width / 2f + rOuter * cos(rad)).toFloat()
              val endY = (size.height / 2f + rOuter * sin(rad)).toFloat()

              drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx()
              )
            }
          }

          // Inner Speed & Unit Display (Genesis & Apple CarPlay Clean Dial)
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "$speed",
              fontSize = speedFontSize,
              fontWeight = FontWeight.Black,
              color = if (isOverSpeed) CrimsonAlert else TextPrimary,
              letterSpacing = (-1.5).sp,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "km/h",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextMuted,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }

          // Korean Speed Limit Circle Badge (Top Right)
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(top = 2.dp, end = 2.dp)
              .size(38.dp)
              .clip(CircleShape)
              .background(Color.White)
              .border(3.dp, Color(0xFFDC2626), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$speedLimit",
              fontSize = 14.sp,
              fontWeight = FontWeight.ExtraBold,
              color = TextPrimary,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // G-Force Indicator Bar (급가속 / 급제동 감지 센서)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "급제동 · 급출발 센서",
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = String.format("%.2f G", gForce),
            fontSize = 12.sp,
            color = if (gForce < -0.4f) CrimsonAlert else if (gForce > 0.4f) AmberWarning else EmeraldSafe,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Responsive Dual-Directional G-Force Meter using Canvas (adapts to any Galaxy S24 width)
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(LightSurfaceSubtle)
        ) {
          val centerX = size.width / 2f
          val halfWidth = size.width / 2f
          val fraction = (gForce.coerceIn(-0.8f, 0.8f) / 0.8f)

          if (fraction < 0f) {
            val barW = (-fraction) * halfWidth
            val barColor = if (gForce < -0.4f) CrimsonAlert else AmberWarning
            drawRoundRect(
              color = barColor,
              topLeft = Offset(centerX - barW, 0f),
              size = Size(barW, size.height),
              cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
          } else if (fraction > 0f) {
            val barW = fraction * halfWidth
            val barColor = if (gForce > 0.4f) AmberWarning else CyanNeon
            drawRoundRect(
              color = barColor,
              topLeft = Offset(centerX, 0f),
              size = Size(barW, size.height),
              cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
          }

          // Center Marker Line
          drawLine(
            color = TextSecondary,
            start = Offset(centerX, 0f),
            end = Offset(centerX, size.height),
            strokeWidth = 2.dp.toPx()
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "◀ 급제동 주의",
            fontSize = 10.sp,
            color = TextMuted,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
          Text(
            text = "부드러운 운전 구간",
            fontSize = 10.sp,
            color = EmeraldSafe,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
          Text(
            text = "급출발 주의 ▶",
            fontSize = 10.sp,
            color = TextMuted,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }
  }
}
