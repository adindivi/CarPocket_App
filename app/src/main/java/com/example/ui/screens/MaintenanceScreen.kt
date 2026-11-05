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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VehicleConsumable
import com.example.ui.components.ModernConfirmDialog
import com.example.ui.components.ModernToastPill
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import com.example.util.toFormattedComma
import com.example.viewmodel.SafeCoachViewModel

@Composable
fun MaintenanceScreen(
  viewModel: SafeCoachViewModel,
  modifier: Modifier = Modifier
) {
  val consumables by viewModel.consumables.collectAsState()
  val cockpitState by viewModel.cockpitState.collectAsState()
  var filterTab by remember { mutableStateOf(0) }
  var confirmingConsumable by remember { mutableStateOf<VehicleConsumable?>(null) }
  var maintenanceToastMessage by remember { mutableStateOf<String?>(null) }

  val filteredConsumables = when (filterTab) {
    1 -> consumables.filter { it.isUrgent }
    2 -> consumables.filter { !it.isUrgent }
    else -> consumables
  }

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
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = horizontalPad),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Left Column: Vehicle Health & Specs Overview
        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            VehicleOverviewCard(
              cockpitState = cockpitState,
              consumables = consumables
            )
          }
        }

        // Right Column: Consumables Inspection & Replacement List
        LazyColumn(
          modifier = Modifier.weight(1.15f),
          contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item { ConsumablesSectionHeader() }
          item {
            ConsumablesFilterTab(
              consumables = consumables,
              filterTab = filterTab,
              onSelectTab = { filterTab = it }
            )
          }
          items(filteredConsumables) { consumable ->
            ConsumableCard(
              item = consumable,
              onMarkReplaced = { confirmingConsumable = consumable }
            )
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
          VehicleOverviewCard(
            cockpitState = cockpitState,
            consumables = consumables
          )
        }
        item { ConsumablesSectionHeader() }
        item {
          ConsumablesFilterTab(
            consumables = consumables,
            filterTab = filterTab,
            onSelectTab = { filterTab = it }
          )
        }
        items(filteredConsumables) { consumable ->
          ConsumableCard(
            item = consumable,
            onMarkReplaced = { confirmingConsumable = consumable }
          )
        }
      }
    }

    // Modern Fintech Confirmation Dialog for Consumable Replacement
    if (confirmingConsumable != null) {
      val item = confirmingConsumable!!
      ModernConfirmDialog(
        title = "${item.name} 교체를 완료하셨나요?",
        subtitle = "오늘 날짜와 누적 주행거리(${cockpitState.totalOdometerKm.toFormattedComma()}km)로 교체 이력이 저장되고 잔여 수명이 100%로 갱신돼요.",
        confirmButtonText = "교체 완료하기",
        cancelButtonText = "취소",
        icon = Icons.Default.Build,
        iconTint = CyanNeon,
        iconBgColor = CyanGlow,
        detailRows = listOf(
          "교체 대상" to item.name,
          "현재 수명" to "${item.healthPercent}%",
          "권장 교체 주기" to "${item.targetIntervalKm.toFormattedComma()} km",
          "교체 후 상태" to "100% (양호)"
        ),
        onConfirm = {
          viewModel.replaceConsumable(item.id)
          maintenanceToastMessage = "${item.name} 교체 이력이 안전하게 저장되었어요"
          confirmingConsumable = null
        },
        onDismiss = { confirmingConsumable = null }
      )
    }

    // Modern Floating Dynamic Island Pill Toast
    if (maintenanceToastMessage != null) {
      ModernToastPill(
        message = maintenanceToastMessage!!,
        onDismiss = { maintenanceToastMessage = null },
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
      )
    }
  }
}

@Composable
private fun VehicleOverviewCard(
  cockpitState: com.example.viewmodel.CockpitUiState,
  consumables: List<VehicleConsumable>
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("vehicle_overview_card"),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // 1행: 가변 타이틀 ── 고정 상태 뱃지 (Anti-Squishing)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            text = "내 차 상태",
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = cockpitState.vehicleModel,
            fontSize = 15.sp,
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
            .clip(RoundedCornerShape(8.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "내 차와 잘 연결됐어요",
            fontSize = 10.sp,
            color = EmeraldSafe,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "총 주행거리",
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = cockpitState.totalOdometerKm.toFormattedComma(),
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = TextPrimary,
              style = NoFontPaddingTextStyle
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "km",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondary,
              modifier = Modifier.padding(bottom = 2.dp),
              style = NoFontPaddingTextStyle
            )
          }
        }

        val urgentCount = consumables.count { it.isUrgent }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (urgentCount > 0) CrimsonGlow else EmeraldGlow)
            .border(
              1.dp,
              if (urgentCount > 0) CrimsonAlert.copy(alpha = 0.4f) else EmeraldSafe.copy(alpha = 0.4f),
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (urgentCount > 0) "${urgentCount}개 점검 필요" else "모두 이상 없어요",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (urgentCount > 0) CrimsonAlert else EmeraldSafe,
            softWrap = false,
            maxLines = 1,
            style = NoFontPaddingTextStyle
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(LightSurfaceSubtle)
          .padding(10.dp)
      ) {
        Text(
          text = "평소에 부드럽게 멈추셔서 브레이크 패드를 30% 더 오래 쓸 수 있어요.",
          fontSize = 11.sp,
          color = TextSecondary,
          lineHeight = 16.sp,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}

@Composable
private fun ConsumablesSectionHeader() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "소모품 관리",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = NoFontPaddingTextStyle
    )
    Text(
      text = "운전습관 맞춤",
      fontSize = 11.sp,
      color = TextMuted,
      softWrap = false,
      maxLines = 1,
      style = NoFontPaddingTextStyle
    )
  }
}

@Composable
private fun ConsumablesFilterTab(
  consumables: List<VehicleConsumable>,
  filterTab: Int,
  onSelectTab: (Int) -> Unit
) {
  val urgentCount = consumables.count { it.isUrgent }
  val normalCount = consumables.size - urgentCount
  val filterOptions = listOf(
    0 to "전체 (${consumables.size})",
    1 to "점검 필요 ($urgentCount)",
    2 to "양호 ($normalCount)"
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(LightSurfaceSubtle)
      .padding(3.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    filterOptions.forEach { (tabIndex, label) ->
      val isSelected = filterTab == tabIndex
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) LightSurface else Color.Transparent)
          .border(
            1.dp,
            if (isSelected) LightCardBorder else Color.Transparent,
            RoundedCornerShape(8.dp)
          )
          .clickable { onSelectTab(tabIndex) }
          .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = label,
          fontSize = 11.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          color = if (isSelected) CyanNeon else TextSecondary,
          softWrap = false,
          maxLines = 1,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}

@Composable
fun ConsumableCard(
  item: VehicleConsumable,
  onMarkReplaced: () -> Unit
) {
  val isOverdue = item.dDayDays < 0
  val isNear = item.dDayDays in 0..20

  val statusColor = when {
    isOverdue -> CrimsonAlert
    isNear -> AmberWarning
    else -> EmeraldSafe
  }

  val statusBg = when {
    isOverdue -> CrimsonGlow
    isNear -> AmberGlow
    else -> LightSurfaceSubtle
  }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1행: 가변 명칭 ── 고정 D-Day 뱃지 (Anti-Squishing)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.name,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          modifier = Modifier.weight(1f, fill = false),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(statusBg)
            .border(
              1.dp,
              if (isOverdue) CrimsonAlert.copy(alpha = 0.5f) else LightCardBorder,
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (isOverdue) "${-item.dDayDays}일 지났어요" else if (item.dDayDays == 0) "오늘 교체 권장" else "${item.dDayDays}일 남음",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            fontFamily = FontFamily.Monospace,
            softWrap = false,
            maxLines = 1,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress Bar
      val progress = (item.currentKm.toFloat() / item.targetIntervalKm).coerceIn(0f, 1f)
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = statusColor,
        trackColor = LightSurfaceSubtle
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${item.currentKm.toFormattedComma()} km / ${item.targetIntervalKm.toFormattedComma()} km",
          fontSize = 11.sp,
          color = TextSecondary,
          fontFamily = FontFamily.Monospace,
          style = NoFontPaddingTextStyle
        )
        Text(
          text = "남은 수명 ${item.healthPercent}%",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = statusColor,
          softWrap = false,
          maxLines = 1,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tip & Replace Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.maintenanceTip,
          fontSize = 11.sp,
          color = TextMuted,
          lineHeight = 16.sp,
          modifier = Modifier
            .weight(1f, fill = false)
            .padding(end = 8.dp),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )

        if (item.isUrgent) {
          Button(
            onClick = onMarkReplaced,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = CrimsonAlert,
              contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Text(
              text = "교체하기",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(LightSurfaceSubtle)
              .border(1.dp, LightCardBorder, RoundedCornerShape(6.dp))
              .clickable(onClick = onMarkReplaced)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "기록하기",
              fontSize = 10.sp,
              color = TextSecondary,
              fontWeight = FontWeight.Medium,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }
    }
  }
}
