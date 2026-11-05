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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IssuedCoupon
import com.example.ui.components.DuotoneRewardBadge
import com.example.ui.components.RewardArtworkType
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldStar
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import com.example.util.toFormattedPoints
import com.example.viewmodel.CockpitUiState

@Composable
fun MyCouponWalletCard(
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
fun PointExchangeCard(
  cockpitState: CockpitUiState,
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
fun RewardExchangeRow(
  icon: ImageVector,
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
          overflow = TextOverflow.Ellipsis,
          style = NoFontPaddingTextStyle
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
