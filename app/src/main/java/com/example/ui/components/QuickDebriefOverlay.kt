package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
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
import com.example.model.QuickDebriefData

/**
 * 3초 마음 진정 케어 오버레이 (보복운전/감정 격앙 방지)
 * 끼어들기 및 급제동 시 운전자의 감정을 이완시키기 위한 호흡 가이드 다이얼로그.
 *
 * @param onDismiss 닫기 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun RoadRageCalmOverlay(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "breathing")
  val scale by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .widthIn(max = 420.dp),
    shape = RoundedCornerShape(20.dp),
    color = Color(0xFF0F172A),
    shadowElevation = 8.dp,
    border = BorderStroke(1.5.dp, CyanNeon.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CyanNeon.copy(alpha = 0.2f))
            .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "3초 마음 진정 케어",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            softWrap = false,
            maxLines = 1,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "닫기",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Pulsing Breathing Circle
      Box(
        modifier = Modifier
          .size(80.dp)
          .scale(scale)
          .clip(CircleShape)
          .background(CyanNeon.copy(alpha = 0.15f))
          .border(2.dp, CyanNeon, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "심호흡 3초",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
          Text(
            text = "천천히 내쉬기",
            fontSize = 9.sp,
            color = CyanNeon,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "방금 상황에 많이 놀라셨죠?",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "심호흡 한 번 깊게 쉬고, 앞차와 거리를 조금 띄워볼까요? 천천히 가도 괜찮아요.",
        fontSize = 12.sp,
        color = Color(0xFFCBD5E1),
        textAlign = TextAlign.Center,
        lineHeight = 17.sp,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )
    }
  }
}

/**
 * 퀵 디브리핑 (Quick Debrief) 카드 오버레이
 * 주행 종료 즉시 나타나 오늘의 점수, 아낀 금액, 칭찬 및 원포인트 팁을 요약 전달.
 *
 * @param debrief 디브리핑 데이터 (점수, 거리, 연료절감액, 칭찬/팁)
 * @param onReplay 음성 브리핑 다시 듣기 콜백
 * @param onDismiss 확인/닫기 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun QuickDebriefCardOverlay(
  debrief: QuickDebriefData,
  onReplay: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .widthIn(max = 500.dp),
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    shadowElevation = 8.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(EmeraldSafe)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "오늘의 주행 요약",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "닫기",
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 주행 핵심 수치 요약
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          color = LightSurfaceSubtle,
          border = BorderStroke(1.dp, LightCardBorder)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "오늘 점수",
              fontSize = 10.sp,
              color = TextMuted,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${debrief.finalScore}점",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (debrief.finalScore >= 80) EmeraldSafe else AmberWarning,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }

        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          color = LightSurfaceSubtle,
          border = BorderStroke(1.dp, LightCardBorder)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "달린 거리/시간",
              fontSize = 10.sp,
              color = TextMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${String.format("%.1f", debrief.distanceKm)}km (${debrief.durationMinutes}분)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              softWrap = false,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }

        Surface(
          modifier = Modifier.weight(1.2f),
          shape = RoundedCornerShape(10.dp),
          color = LightSurfaceSubtle,
          border = BorderStroke(1.dp, LightCardBorder)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "아낀 돈 & 포인트",
              fontSize = 10.sp,
              color = TextMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "+${debrief.fuelSavedWon}원 / +${debrief.earnedPoints}P",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CyanNeon,
              softWrap = false,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 칭찬 카드 (녹색)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = EmeraldGlow.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, EmeraldSafe.copy(alpha = 0.35f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = EmeraldSafe,
            modifier = Modifier
              .size(15.dp)
              .padding(top = 1.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "오늘 잘한 점",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldSafe,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = debrief.praiseComment,
              fontSize = 12.sp,
              color = TextPrimary,
              lineHeight = 16.sp,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 원포인트 팁 카드 (블루)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CyanGlow.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.35f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = CyanNeon,
            modifier = Modifier
              .size(15.dp)
              .padding(top = 1.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "다음 주행 팁",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = CyanNeon,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = debrief.onePointTip,
              fontSize = 12.sp,
              color = TextPrimary,
              lineHeight = 16.sp,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onReplay,
          modifier = Modifier
            .weight(1f)
            .height(38.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = LightSurfaceSubtle,
            contentColor = TextPrimary
          ),
          border = BorderStroke(1.dp, LightCardBorder),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = CyanNeon
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "소리로 듣기",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .weight(1f)
            .height(38.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = CyanNeon,
            contentColor = Color.White
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Text(
            text = "확인",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }
  }
}
