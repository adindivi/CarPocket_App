package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommuteChallenge
import com.example.model.InsuranceCompanyQuote
import com.example.model.IssuedCoupon
import com.example.ui.components.CarPocketCoinVaultIcon
import com.example.ui.components.DuotoneRewardBadge
import com.example.ui.components.RewardArtworkType
import com.example.ui.components.ModernCouponBarcodeDialog
import com.example.ui.components.ModernFeedbackDialog
import com.example.ui.components.ModernToastPill
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
import com.example.util.toFormattedPoints
import com.example.viewmodel.SafeCoachViewModel

enum class InsuranceTab(val title: String) {
  DISCOUNT("보험료 할인 비교"),
  CHALLENGES("출퇴근 챌린지 · 쿠폰함")
}

@Composable
fun InsuranceScreen(
  viewModel: SafeCoachViewModel,
  modifier: Modifier = Modifier
) {
  val quotes by viewModel.insuranceQuotes.collectAsState()
  val cockpitState by viewModel.cockpitState.collectAsState()
  val commuteChallenges by viewModel.commuteChallenges.collectAsState()
  val issuedCoupons by viewModel.issuedCoupons.collectAsState()

  var selectedTab by remember { mutableStateOf(InsuranceTab.DISCOUNT) }
  var showClaimSuccessModal by remember { mutableStateOf<String?>(null) }
  var showExchangeMessage by remember { mutableStateOf<String?>(null) }
  var selectedCouponForBarcode by remember { mutableStateOf<IssuedCoupon?>(null) }
  var toastFeedbackMessage by remember { mutableStateOf<String?>(null) }

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
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = horizontalPad)
      ) {
        Spacer(modifier = Modifier.height(12.dp))
        InsuranceTabSelector(
          selectedTab = selectedTab,
          onSelectTab = { selectedTab = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          if (selectedTab == InsuranceTab.DISCOUNT) {
            LazyColumn(
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(bottom = 24.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item { InsuranceSummaryCard(currentScore = cockpitState.currentTripScore) }
            }
            LazyColumn(
              modifier = Modifier.weight(1.15f),
              contentPadding = PaddingValues(bottom = 24.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item { InsuranceQuotesHeader(quoteCount = quotes.size) }
              items(quotes) { quote ->
                InsuranceQuoteCard(
                  quote = quote,
                  onApply = { showClaimSuccessModal = quote.companyName }
                )
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(bottom = 24.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item { CommuteChallengePromotionCard() }
              item { CommuteChallengesHeader() }
              items(commuteChallenges) { challenge ->
                CommuteChallengeCard(
                  challenge = challenge,
                  onClaim = {
                    viewModel.claimChallengeCoupon(challenge.id)
                    toastFeedbackMessage = "${challenge.rewardTag} 쿠폰이 발급되었어요"
                  }
                )
              }
            }
            LazyColumn(
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(bottom = 24.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                MyCouponWalletCard(
                  coupons = issuedCoupons,
                  onUseCoupon = { coupon -> selectedCouponForBarcode = coupon }
                )
              }
              item {
                PointExchangeCard(
                  cockpitState = cockpitState,
                  showExchangeMessage = showExchangeMessage,
                  onExchange = { pts, name ->
                    val ok = viewModel.exchangeReward(pts, name)
                    if (ok) {
                      showExchangeMessage = "$name 교환 완료"
                      toastFeedbackMessage = "$name 교환이 완료되었어요"
                    }
                  }
                )
              }
            }
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
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          InsuranceTabSelector(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it }
          )
        }

        if (selectedTab == InsuranceTab.DISCOUNT) {
          item { InsuranceSummaryCard(currentScore = cockpitState.currentTripScore) }
          item { InsuranceQuotesHeader(quoteCount = quotes.size) }
          items(quotes) { quote ->
            InsuranceQuoteCard(
              quote = quote,
              onApply = { showClaimSuccessModal = quote.companyName }
            )
          }
        } else {
          item { CommuteChallengePromotionCard() }
          item { CommuteChallengesHeader() }
          items(commuteChallenges) { challenge ->
            CommuteChallengeCard(
              challenge = challenge,
              onClaim = {
                viewModel.claimChallengeCoupon(challenge.id)
                toastFeedbackMessage = "${challenge.rewardTag} 쿠폰이 발급되었어요"
              }
            )
          }
          item {
            MyCouponWalletCard(
              coupons = issuedCoupons,
              onUseCoupon = { coupon -> selectedCouponForBarcode = coupon }
            )
          }
          item {
            PointExchangeCard(
              cockpitState = cockpitState,
              showExchangeMessage = showExchangeMessage,
              onExchange = { pts, name ->
                val ok = viewModel.exchangeReward(pts, name)
                if (ok) {
                  showExchangeMessage = "$name 교환 완료"
                  toastFeedbackMessage = "$name 교환이 완료되었어요"
                }
              }
            )
          }
        }
      }
    }

    // Modern Fintech Modal Popup on Insurance Discount Application
    if (showClaimSuccessModal != null) {
      ModernFeedbackDialog(
        icon = Icons.Default.Check,
        iconTint = EmeraldSafe,
        iconBgColor = EmeraldGlow,
        title = "${showClaimSuccessModal} 할인 신청 완료",
        subtitle = "안전운전 점수 증명서가 보험사로 전송되었어요. 다음 갱신 때 최대 16% 할인이 바로 적용돼요.",
        primaryButtonText = "확인",
        primaryButtonColor = CyanNeon,
        onPrimaryClick = { showClaimSuccessModal = null },
        onDismiss = { showClaimSuccessModal = null },
        testTag = "insurance_claim_success_dialog"
      ) {
        // Structured Detail Summary Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("제휴 보험사", fontSize = 12.sp, color = TextMuted, style = NoFontPaddingTextStyle)
              Text(
                showClaimSuccessModal ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                style = NoFontPaddingTextStyle
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("안전운전 점수", fontSize = 12.sp, color = TextMuted, style = NoFontPaddingTextStyle)
              Text(
                "${cockpitState.currentTripScore}점 (퍼펙트)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldSafe,
                style = NoFontPaddingTextStyle
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("연간 예상 할인액", fontSize = 12.sp, color = TextMuted, style = NoFontPaddingTextStyle)
              Text(
                "약 196,800원",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = CyanNeon,
                fontFamily = FontFamily.Monospace,
                style = NoFontPaddingTextStyle
              )
            }
          }
        }
      }
    }

    // Modern Coupon Barcode Dialog
    if (selectedCouponForBarcode != null) {
      val coupon = selectedCouponForBarcode!!
      ModernCouponBarcodeDialog(
        coupon = coupon,
        onUseNow = {
          viewModel.useIssuedCoupon(coupon.id)
          toastFeedbackMessage = "${coupon.title} 사용이 완료되었어요"
          selectedCouponForBarcode = null
        },
        onDismiss = { selectedCouponForBarcode = null }
      )
    }

    // Modern Floating Dynamic Island Pill Toast
    if (toastFeedbackMessage != null) {
      ModernToastPill(
        message = toastFeedbackMessage!!,
        onDismiss = { toastFeedbackMessage = null },
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
      )
    }
  }
}

@Composable
private fun InsuranceTabSelector(
  selectedTab: InsuranceTab,
  onSelectTab: (InsuranceTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    color = LightSurfaceSubtle,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      InsuranceTab.entries.forEach { tab ->
        val isSelected = (selectedTab == tab)
        Box(
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CyanNeon else Color.Transparent)
            .clickable { onSelectTab(tab) },
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = tab.title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }
    }
  }
}

@Composable
private fun InsuranceSummaryCard(currentScore: Int) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("insurance_summary_card"),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            text = "내 안전운전 점수 100점",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "매년 최대 196,800원 절약",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
          modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CyanGlow)
            .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${currentScore}점 달성",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(LightSurfaceSubtle)
          .border(1.dp, LightCardBorder, RoundedCornerShape(12.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              text = "1년 기준",
              fontSize = 11.sp,
              color = TextMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "돌려받는 보험료",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }
          Text(
            text = "약 196,800원",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = CyanNeon,
            fontFamily = FontFamily.Monospace,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(EmeraldGlow)
            .border(1.dp, EmeraldSafe.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "4대 보험사 제휴",
              fontSize = 11.sp,
              color = EmeraldSafe,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CyanGlow)
            .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "최대 16% 즉시 할인",
              fontSize = 11.sp,
              color = CyanNeon,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }
    }
  }
}

@Composable
private fun InsuranceQuotesHeader(quoteCount: Int) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "내 점수로 할인받는 곳",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = NoFontPaddingTextStyle
    )
    Text(
      text = "총 ${quoteCount}개 제휴사",
      fontSize = 11.sp,
      color = TextMuted,
      softWrap = false,
      maxLines = 1,
      style = NoFontPaddingTextStyle
    )
  }
}

@Composable
private fun CommuteChallengesHeader() {
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
private fun CommuteChallengePromotionCard() {
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
private fun CommuteChallengeCard(
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

@Composable
private fun MyCouponWalletCard(
  coupons: List<IssuedCoupon>,
  onUseCoupon: (IssuedCoupon) -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "내 발급 쿠폰함",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            style = NoFontPaddingTextStyle
          )
        }
        Text(
          text = "보유 ${coupons.count { !it.isUsed }}장",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = CyanNeon,
          style = NoFontPaddingTextStyle
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      coupons.forEach { coupon ->
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(10.dp),
          color = if (coupon.isUsed) LightSurfaceSubtle else LightSurface,
          border = BorderStroke(1.dp, if (coupon.isUsed) LightCardBorder else CyanNeon.copy(alpha = 0.35f))
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              val couponArt = when {
                coupon.title.contains("주유") -> RewardArtworkType.FUEL
                coupon.title.contains("세차") -> RewardArtworkType.CAR_WASH
                coupon.title.contains("아메리카노") || coupon.title.contains("스타벅스") || coupon.title.contains("커피") -> RewardArtworkType.COFFEE
                coupon.title.contains("충전") -> RewardArtworkType.EV_CHARGING
                else -> RewardArtworkType.WELCOME
              }
              Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
              ) {
                DuotoneRewardBadge(type = couponArt, size = 26.dp, iconSize = 14.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = coupon.title,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (coupon.isUsed) TextMuted else TextPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  style = NoFontPaddingTextStyle
                )
              }
              Text(
                text = coupon.expiryDate,
                fontSize = 10.sp,
                color = TextMuted,
                softWrap = false,
                maxLines = 1,
                style = NoFontPaddingTextStyle
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "BARCODE: ${coupon.barcodeNumber}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = if (coupon.isUsed) TextMuted else TextSecondary,
                style = NoFontPaddingTextStyle
              )

              if (coupon.isUsed) {
                Text(
                  text = "사용완료",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextMuted,
                  style = NoFontPaddingTextStyle
                )
              } else {
                Button(
                  onClick = { onUseCoupon(coupon) },
                  shape = RoundedCornerShape(6.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.White),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.height(26.dp)
                ) {
                  Text(
                    text = "지금 사용하기",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = NoFontPaddingTextStyle
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PointExchangeCard(
  cockpitState: com.example.viewmodel.CockpitUiState,
  showExchangeMessage: String?,
  onExchange: (Int, String) -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("points_wallet_card"),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(GoldBadgeBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Savings, contentDescription = null, tint = GoldStar, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("내가 모은 포인트", fontSize = 11.sp, color = TextMuted, style = NoFontPaddingTextStyle)
            Text(
              text = cockpitState.accumulatedPoints.toFormattedPoints(),
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = GoldStar,
              fontFamily = FontFamily.Monospace,
              style = NoFontPaddingTextStyle
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyanGlow)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("1km 안전운전마다 50P", fontSize = 10.sp, color = CyanNeon, fontWeight = FontWeight.Bold, style = NoFontPaddingTextStyle)
        }
      }

      if (showExchangeMessage != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(EmeraldGlow)
            .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
          Text(
            text = "🎁 $showExchangeMessage (내 쿠폰함에서 확인)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldSafe,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      Text("포인트로 즉시 교환", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, style = NoFontPaddingTextStyle)
      Spacer(modifier = Modifier.height(8.dp))

      RewardExchangeRow(
        icon = Icons.Default.LocalGasStation,
        title = "GS칼텍스 주유쿠폰 1만원권",
        points = 10000,
        canAfford = cockpitState.accumulatedPoints >= 10000,
        onExchange = { onExchange(10000, "GS칼텍스 주유쿠폰 1만원권") }
      )

      Spacer(modifier = Modifier.height(8.dp))

      RewardExchangeRow(
        icon = Icons.Default.CardGiftcard,
        title = "오토오아시스 자동세차 1회권",
        points = 5000,
        canAfford = cockpitState.accumulatedPoints >= 5000,
        onExchange = { onExchange(5000, "자동세차 1회권") }
      )

      Spacer(modifier = Modifier.height(8.dp))

      RewardExchangeRow(
        icon = Icons.Default.CardGiftcard,
        title = "스타벅스 카페 아메리카노 T",
        points = 4500,
        canAfford = cockpitState.accumulatedPoints >= 4500,
        onExchange = { onExchange(4500, "스타벅스 아메리카노 쿠폰") }
      )
    }
  }
}

@Composable
fun InsuranceQuoteCard(
  quote: InsuranceCompanyQuote,
  onApply: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = quote.companyName,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          modifier = Modifier.weight(1f, fill = false),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = NoFontPaddingTextStyle
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = quote.badgeText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = quote.specialContractName,
        fontSize = 12.sp,
        color = TextMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = NoFontPaddingTextStyle
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = quote.benefitsSummary,
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 16.sp,
        style = NoFontPaddingTextStyle
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Text(
            text = "운전점수 ${quote.minScoreRequired}점 이상이면 할인",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = EmeraldSafe,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = onApply,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.White),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Text(
            text = "할인 신청하기",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }
    }
  }
}

@Composable
fun RewardExchangeRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  points: Int,
  canAfford: Boolean,
  onExchange: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(LightSurfaceSubtle)
      .border(1.dp, LightCardBorder, RoundedCornerShape(12.dp))
      .padding(10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(CyanGlow),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(17.dp))
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = points.toFormattedPoints(),
          fontSize = 11.sp,
          color = GoldStar,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          style = NoFontPaddingTextStyle
        )
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    Button(
      onClick = onExchange,
      enabled = canAfford,
      shape = RoundedCornerShape(8.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (canAfford) CyanNeon else LightCardBorder,
        contentColor = Color.White,
        disabledContainerColor = LightCardBorder,
        disabledContentColor = TextMuted
      ),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
      modifier = Modifier.height(34.dp)
    ) {
      Text(
        text = if (canAfford) "교환하기" else "포인트 부족",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        style = NoFontPaddingTextStyle
      )
    }
  }
}
