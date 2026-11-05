package com.example.ui.components.cockpit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.TelematicsConstants
import com.example.ui.components.CarPocketCoinVaultIcon
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle

@Composable
fun FuelEconomyCurrencyBadge(
  liveFuelSavedWon: Int,
  liveAggressiveCostWon: Int,
  recentFloatingMessage: String?,
  isDriving: Boolean
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    border = BorderStroke(1.dp, LightCardBorder),
    shadowElevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CarPocketCoinVaultIcon(size = 36.dp)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "카포켓 연비 저금통",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = NoFontPaddingTextStyle
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isDriving) "기름값 아끼는 중" else "주행 대기",
                fontSize = 10.sp,
                color = if (isDriving) EmeraldSafe else TextMuted,
                maxLines = 1,
                softWrap = false,
                style = NoFontPaddingTextStyle
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            val comparisonText = TelematicsConstants.formatFuelSavingComparison(liveFuelSavedWon)
            Text(
              text = comparisonText,
              fontSize = 12.sp,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 저금 요약 뱃지 (Pill)
        Box(
          modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (liveFuelSavedWon > 0) EmeraldGlow else LightSurfaceSubtle)
            .border(1.dp, if (liveFuelSavedWon > 0) EmeraldSafe.copy(alpha = 0.4f) else LightCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (liveFuelSavedWon > 0) "+${liveFuelSavedWon}원 저금" else "0원 저금",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (liveFuelSavedWon > 0) EmeraldSafe else TextMuted,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      // 급조작 비용 알림 (발생 시 즉각 애니메이션 표시)
      AnimatedVisibility(
        visible = recentFloatingMessage != null || liveAggressiveCostWon > 0,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
      ) {
        Column {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = CrimsonGlow.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.35f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = recentFloatingMessage ?: "급조작으로 연료가 더 쓰였어요",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CrimsonAlert,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = NoFontPaddingTextStyle
              )
              if (liveAggressiveCostWon > 0) {
                Text(
                  text = "-${liveAggressiveCostWon}원",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = CrimsonAlert,
                  softWrap = false,
                  maxLines = 1,
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
