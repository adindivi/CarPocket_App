package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IssuedCoupon
import com.example.ui.components.ModernCouponBarcodeDialog
import com.example.ui.components.ModernFeedbackDialog
import com.example.ui.components.ModernToastPill
import com.example.ui.components.insurance.CommuteChallengeCard
import com.example.ui.components.insurance.CommuteChallengePromotionCard
import com.example.ui.components.insurance.CommuteChallengesHeader
import com.example.ui.components.insurance.InsuranceQuoteCard
import com.example.ui.components.insurance.InsuranceQuotesHeader
import com.example.ui.components.insurance.InsuranceSummaryCard
import com.example.ui.components.insurance.InsuranceTab
import com.example.ui.components.insurance.InsuranceTabSelector
import com.example.ui.components.insurance.MyCouponWalletCard
import com.example.ui.components.insurance.PointExchangeCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.util.NoFontPaddingTextStyle
import com.example.viewmodel.SafeCoachViewModel

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
