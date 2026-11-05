package com.example.ui.components.insurance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.util.NoFontPaddingTextStyle

enum class InsuranceTab(val title: String) {
  DISCOUNT("보험료 할인 비교"),
  CHALLENGES("출퇴근 챌린지 · 쿠폰함")
}

@Composable
fun InsuranceTabSelector(
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
fun InsuranceSummaryCard(currentScore: Int) {
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
