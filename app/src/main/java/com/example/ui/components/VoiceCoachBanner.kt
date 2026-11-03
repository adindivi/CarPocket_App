package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DrivingEvent
import com.example.model.EventSeverity
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.TextPrimary

@Composable
fun VoiceCoachBanner(
  event: DrivingEvent?,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "wave")
  val waveHeight1 by infiniteTransition.animateFloat(
    initialValue = 4f, targetValue = 18f,
    animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse),
    label = "h1"
  )
  val waveHeight2 by infiniteTransition.animateFloat(
    initialValue = 16f, targetValue = 6f,
    animationSpec = infiniteRepeatable(tween(250), RepeatMode.Reverse),
    label = "h2"
  )
  val waveHeight3 by infiniteTransition.animateFloat(
    initialValue = 8f, targetValue = 20f,
    animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
    label = "h3"
  )

  AnimatedVisibility(
    visible = event != null,
    enter = slideInVertically { -it },
    exit = slideOutVertically { -it },
    modifier = modifier
  ) {
    if (event == null) return@AnimatedVisibility

    val bannerColor = when (event.severity) {
      EventSeverity.CRITICAL -> CrimsonAlert
      EventSeverity.WARNING -> AmberWarning
      EventSeverity.INFO -> CyanNeon
    }

    val bannerBg = when (event.severity) {
      EventSeverity.CRITICAL -> CrimsonGlow
      EventSeverity.WARNING -> AmberGlow
      EventSeverity.INFO -> CyanGlow
    }

    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("voice_coach_banner"),
      shape = RoundedCornerShape(16.dp),
      color = LightSurface,
      shadowElevation = 2.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Voice Wave / Speaker Indicator
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(bannerBg),
          contentAlignment = Alignment.Center
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(modifier = Modifier.width(3.dp).height(waveHeight1.dp).clip(CircleShape).background(bannerColor))
            Box(modifier = Modifier.width(3.dp).height(waveHeight2.dp).clip(CircleShape).background(bannerColor))
            Box(modifier = Modifier.width(3.dp).height(waveHeight3.dp).clip(CircleShape).background(bannerColor))
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(bannerBg)
                .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
              Text(
                text = "AI 음성 코칭",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = bannerColor,
                softWrap = false,
                maxLines = 1,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = event.title,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false),
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "“${event.voiceSpeech}”",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            lineHeight = 18.sp,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }
  }
}
