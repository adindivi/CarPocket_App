package com.example.ui.components.cockpit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnforcementCamera
import com.example.model.ParkingFeeData
import com.example.model.ParkingGracePeriodState
import com.example.model.ParkingMemoryData
import com.example.model.VehicleGpsCoordinate
import com.example.ui.components.CctvEnforcementMapView
import com.example.ui.components.ParkingFeeTranslatorCard
import com.example.ui.components.ParkingGraceTimerCard
import com.example.ui.components.SmartParkingMemoryCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle

enum class SmartParkingTab(val label: String, val tag: String) {
  ENFORCEMENT_MAP("단속 방어 지도", "tab_parking_enforcement"),
  PILLAR_MEMORY("내 차 위치 메모", "tab_parking_memory"),
  FEE_CALCULATOR("주차 요금 계산", "tab_parking_fee")
}

@Composable
fun SmartParkingDefenseSection(
  selectedTab: SmartParkingTab,
  onSelectTab: (SmartParkingTab) -> Unit,
  // Tab 1: Enforcement & Grace
  cctvCameras: List<EnforcementCamera>,
  nearestCctv: EnforcementCamera?,
  vehicleCoordinate: VehicleGpsCoordinate,
  isStationary: Boolean,
  graceState: ParkingGracePeriodState,
  onSelectCctvCamera: (EnforcementCamera) -> Unit,
  onOpenCctvList: () -> Unit,
  onMoveVehicleComplete: () -> Unit,
  onResetGraceTimer: () -> Unit,
  onSimulate5MinWarning: () -> Unit,
  // Tab 2: Pillar Memory
  parkingMemory: ParkingMemoryData,
  onScanPillar: () -> Unit,
  onAskWhereIsMyCar: () -> Unit,
  // Tab 3: Parking Fee
  feeData: ParkingFeeData,
  onSelectHours: (Int) -> Unit,
  onSpeakBriefing: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Segmented Control Tab Header (User Rule 3 준수)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(LightSurfaceSubtle)
        .border(1.dp, LightCardBorder, RoundedCornerShape(10.dp))
        .padding(3.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      SmartParkingTab.entries.forEach { tab ->
        val isSelected = selectedTab == tab
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CyanNeon else Color.Transparent)
            .clickable { onSelectTab(tab) }
            .padding(vertical = 8.dp)
            .testTag(tab.tag),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = tab.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) Color.White else TextSecondary,
            maxLines = 1,
            softWrap = false,
            style = NoFontPaddingTextStyle
          )
        }
      }
    }

    // 2. Active Tab Content
    when (selectedTab) {
      SmartParkingTab.ENFORCEMENT_MAP -> {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          if (graceState.isTracking) {
            ParkingGraceTimerCard(
              graceState = graceState,
              onMoveVehicleComplete = onMoveVehicleComplete,
              onResetTimer = onResetGraceTimer,
              onSimulate5MinWarning = onSimulate5MinWarning
            )
          }
          CctvEnforcementMapView(
            cameras = cctvCameras,
            selectedCamera = nearestCctv,
            vehicleCoordinate = vehicleCoordinate,
            isStationary = isStationary,
            onSelectCamera = onSelectCctvCamera,
            onOpenFullList = onOpenCctvList
          )
        }
      }
      SmartParkingTab.PILLAR_MEMORY -> {
        SmartParkingMemoryCard(
          parkingMemory = parkingMemory,
          onScanPillar = onScanPillar,
          onAskWhereIsMyCar = onAskWhereIsMyCar
        )
      }
      SmartParkingTab.FEE_CALCULATOR -> {
        ParkingFeeTranslatorCard(
          feeData = feeData,
          onSelectHours = onSelectHours,
          onSpeakBriefing = onSpeakBriefing
        )
      }
    }
  }
}
