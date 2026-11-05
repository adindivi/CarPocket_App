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
import com.example.model.InsuranceCompanyQuote
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
fun InsuranceQuotesHeader(quoteCount: Int) {
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
