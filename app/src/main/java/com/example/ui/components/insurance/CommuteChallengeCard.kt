package com.example.ui.components.insurance

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommuteChallenge
import com.example.ui.components.CarPocketCoinVaultIcon
import com.example.ui.components.DuotoneRewardBadge
import com.example.ui.components.RewardArtworkType
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
fun CommuteChallengesHeader() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "도전 중인 안전 챌린지",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = NoFontPaddingTextStyle
    )
    Text(
      text = "완료 시 즉시 쿠폰 지급",
      fontSize = 11.sp,
      color = CyanNeon,
      fontWeight = FontWeight.SemiBold,
      softWrap = false,
      maxLines = 1,
      style = NoFontPaddingTextStyle
    )
  }
}

@Composable
fun CommuteChallengePromotionCard() {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CarPocketCoinVaultIcon(size = 42.dp)
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "출퇴근 안전 마이크로 리워드",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          style = NoFontPaddingTextStyle
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "1년에 한 번뿐인 보험료 할인 대신, 이번 주 무사고·스쿨존 준수로 주유권과 세차권을 매주 챙겨가세요!",
          fontSize = 11.sp,
          color = TextSecondary,
          lineHeight = 15.sp,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}

@Composable
fun CommuteChallengeCard(
  challenge: CommuteChallenge,
  onClaim: () -> Unit
) {
  val artworkType = when {
    challenge.rewardTag.contains("주유") || challenge.iconEmoji.contains("⛽") -> RewardArtworkType.FUEL
    challenge.rewardTag.contains("세차") || challenge.iconEmoji.contains("🧼") -> RewardArtworkType.CAR_WASH
    challenge.rewardTag.contains("음료") || challenge.rewardTag.contains("카페") || challenge.iconEmoji.contains("☕") -> RewardArtworkType.COFFEE
    challenge.rewardTag.contains("충전") || challenge.iconEmoji.contains("⚡") -> RewardArtworkType.EV_CHARGING
    else -> RewardArtworkType.WELCOME
  }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1행: 가변 타이틀 ── 고정 태그 뱃지
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically
        ) {
          DuotoneRewardBadge(type = artworkType, size = 30.dp, iconSize = 16.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = challenge.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CyanGlow)
            .border(1.dp, CyanNeon.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
          Text(
            text = challenge.rewardTag,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            softWrap = false,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // 2행: 부가 설명
      Text(
        text = challenge.subtitle,
        fontSize = 11.sp,
        color = TextSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = NoFontPaddingTextStyle
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 3행: 진행률 바
      LinearProgressIndicator(
        progress = { challenge.progressPercent },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (challenge.isCompleted) EmeraldSafe else CyanNeon,
        trackColor = LightCardBorder
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 4행: 진행도 라벨 ── 보상 수령 버튼
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (challenge.isCompleted) "달성 완료 (100%)" else "${challenge.currentProgress}/${challenge.targetProgress} 진행 중",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (challenge.isCompleted) EmeraldSafe else TextMuted,
          style = NoFontPaddingTextStyle
        )

        if (challenge.isClaimed) {
          Box(
            modifier = Modifier
              .height(30.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(LightSurfaceSubtle)
              .border(1.dp, LightCardBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "수령 완료 ✓",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = TextMuted,
              softWrap = false,
              style = NoFontPaddingTextStyle
            )
          }
        } else if (challenge.isCompleted) {
          Button(
            onClick = onClaim,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Text(
              text = "🎁 쿠폰 받기",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              style = NoFontPaddingTextStyle
            )
          }
        } else {
          Box(
            modifier = Modifier
              .height(30.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(LightSurfaceSubtle)
              .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = challenge.periodLabel,
              fontSize = 11.sp,
              color = TextSecondary,
              softWrap = false,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }
    }
  }
}
