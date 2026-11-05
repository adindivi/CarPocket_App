package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.TurnSignal
import com.example.ui.components.QuickDebriefCardOverlay
import com.example.ui.components.RoadRageCalmOverlay
import com.example.ui.components.SurroundViewMonitorCluster
import com.example.ui.components.VoiceCoachBanner
import com.example.ui.components.cockpit.CockpitStatusHeader
import com.example.ui.components.cockpit.FuelEconomyCurrencyBadge
import com.example.ui.components.cockpit.LiveDriveMetricsRow
import com.example.ui.components.cockpit.MasterDriveActionButton
import com.example.ui.components.cockpit.SmartParkingDefenseSection
import com.example.ui.components.cockpit.SmartParkingTab
import com.example.ui.theme.LightAppBg
import com.example.viewmodel.SafeCoachViewModel

@Composable
fun DriveCockpitScreen(
  viewModel: SafeCoachViewModel,
  onOpenCctvList: () -> Unit,
  modifier: Modifier = Modifier
) {
  val state by viewModel.cockpitState.collectAsState()
  val cctvCameras by viewModel.cctvCameras.collectAsState()
  val graceState by viewModel.parkingGraceState.collectAsState()
  val parkingMemory by viewModel.parkingMemory.collectAsState()
  val parkingFeeData by viewModel.parkingFeeData.collectAsState()
  var selectedParkingTab by remember { mutableStateOf(SmartParkingTab.ENFORCEMENT_MAP) }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(LightAppBg),
    contentAlignment = Alignment.TopCenter
  ) {
    val isExpanded = maxWidth >= 600.dp
    val horizontalPad = if (maxWidth < 380.dp) 12.dp else 16.dp

    if (isExpanded) {
      // Landscape / Wide Responsive 2-Pane Cockpit Layout (Galaxy S24 Landscape / Fold)
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = horizontalPad),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            CockpitStatusHeader(
              state = state,
              onToggleMute = { viewModel.toggleVoiceMute() },
              onToggleBeepMode = { viewModel.toggleBeepOnlyCoachMode() }
            )
          }
          item { VoiceCoachBanner(event = state.activeBannerMessage) }
          item {
            SurroundViewMonitorCluster(
              speed = state.currentSpeed,
              speedLimit = state.speedLimit,
              gForce = state.gForce,
              turnSignal = state.turnSignal,
              isSeatbeltFastened = state.isSeatbeltFastened,
              isDriving = state.isDriving,
              onLeftSignalClick = { viewModel.setTurnSignal(TurnSignal.LEFT) },
              onRightSignalClick = { viewModel.setTurnSignal(TurnSignal.RIGHT) },
              onSeatbeltToggle = { viewModel.toggleSeatbelt() }
            )
          }
          item {
            FuelEconomyCurrencyBadge(
              liveFuelSavedWon = state.liveFuelSavedWon,
              liveAggressiveCostWon = state.liveAggressiveCostWon,
              recentFloatingMessage = state.recentCostFloatingMessage,
              isDriving = state.isDriving
            )
          }
          item { LiveDriveMetricsRow(state = state) }
          item {
            MasterDriveActionButton(
              isDriving = state.isDriving,
              onToggleDrive = {
                if (state.isDriving) viewModel.stopDriving() else viewModel.startDriving()
              }
            )
          }
        }

        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            SmartParkingDefenseSection(
              selectedTab = selectedParkingTab,
              onSelectTab = { selectedParkingTab = it },
              cctvCameras = cctvCameras,
              nearestCctv = state.nearestCctv,
              vehicleCoordinate = state.vehicleCoordinate,
              isStationary = state.isStationary,
              graceState = graceState,
              onSelectCctvCamera = { viewModel.selectCctvCamera(it) },
              onOpenCctvList = onOpenCctvList,
              onMoveVehicleComplete = { viewModel.moveVehicleComplete() },
              onResetGraceTimer = { viewModel.resetParkingGraceTimer() },
              onSimulate5MinWarning = { viewModel.simulate5MinParkingWarning() },
              parkingMemory = parkingMemory,
              onScanPillar = { viewModel.scanParkingPillar() },
              onAskWhereIsMyCar = { viewModel.sendGleoMessage("내 차 어디 있어?") },
              feeData = parkingFeeData,
              onSelectHours = { viewModel.selectParkingHours(it) },
              onSpeakBriefing = { viewModel.speakParkingFeeBriefing() }
            )
          }
        }
      }
    } else {
      // Portrait Responsive Layout optimized for Galaxy S24 (19.5:9, ~360-412dp width)
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
          .padding(horizontal = horizontalPad),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          CockpitStatusHeader(
            state = state,
            onToggleMute = { viewModel.toggleVoiceMute() },
            onToggleBeepMode = { viewModel.toggleBeepOnlyCoachMode() }
          )
        }
        item { VoiceCoachBanner(event = state.activeBannerMessage) }
        item {
          SurroundViewMonitorCluster(
            speed = state.currentSpeed,
            speedLimit = state.speedLimit,
            gForce = state.gForce,
            turnSignal = state.turnSignal,
            isSeatbeltFastened = state.isSeatbeltFastened,
            isDriving = state.isDriving,
            onLeftSignalClick = { viewModel.setTurnSignal(TurnSignal.LEFT) },
            onRightSignalClick = { viewModel.setTurnSignal(TurnSignal.RIGHT) },
            onSeatbeltToggle = { viewModel.toggleSeatbelt() }
          )
        }
        item {
          FuelEconomyCurrencyBadge(
            liveFuelSavedWon = state.liveFuelSavedWon,
            liveAggressiveCostWon = state.liveAggressiveCostWon,
            recentFloatingMessage = state.recentCostFloatingMessage,
            isDriving = state.isDriving
          )
        }
        item { LiveDriveMetricsRow(state = state) }
        item {
          MasterDriveActionButton(
            isDriving = state.isDriving,
            onToggleDrive = {
              if (state.isDriving) viewModel.stopDriving() else viewModel.startDriving()
            }
          )
        }
        item {
          SmartParkingDefenseSection(
            selectedTab = selectedParkingTab,
            onSelectTab = { selectedParkingTab = it },
            cctvCameras = cctvCameras,
            nearestCctv = state.nearestCctv,
            vehicleCoordinate = state.vehicleCoordinate,
            isStationary = state.isStationary,
            graceState = graceState,
            onSelectCctvCamera = { viewModel.selectCctvCamera(it) },
            onOpenCctvList = onOpenCctvList,
            onMoveVehicleComplete = { viewModel.moveVehicleComplete() },
            onResetGraceTimer = { viewModel.resetParkingGraceTimer() },
            onSimulate5MinWarning = { viewModel.simulate5MinParkingWarning() },
            parkingMemory = parkingMemory,
            onScanPillar = { viewModel.scanParkingPillar() },
            onAskWhereIsMyCar = { viewModel.sendGleoMessage("내 차 어디 있어?") },
            feeData = parkingFeeData,
            onSelectHours = { viewModel.selectParkingHours(it) },
            onSpeakBriefing = { viewModel.speakParkingFeeBriefing() }
          )
        }
      }
    }

    // Road Rage Calm Intervention Modal Scrim Overlay
    AnimatedVisibility(
      visible = state.activeCalmCoachState,
      enter = fadeIn(tween(250)),
      exit = fadeOut(tween(200)),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.52f))
          .padding(horizontal = horizontalPad),
        contentAlignment = Alignment.Center
      ) {
        RoadRageCalmOverlay(
          onDismiss = { /* auto-dismissed or user dismiss */ }
        )
      }
    }

    // 30-Second Quick Debrief Bottom Modal Sheet Scrim
    AnimatedVisibility(
      visible = state.activeQuickDebrief != null,
      enter = fadeIn(tween(250)),
      exit = fadeOut(tween(200)),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.45f))
          .clickable { viewModel.dismissQuickDebrief() },
        contentAlignment = Alignment.BottomCenter
      ) {
        Box(
          modifier = Modifier
            .padding(start = horizontalPad, end = horizontalPad, bottom = 14.dp)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) { /* Intercept click so dialog inside won't dismiss */ }
        ) {
          state.activeQuickDebrief?.let { debrief ->
            QuickDebriefCardOverlay(
              debrief = debrief,
              onReplay = { viewModel.replayQuickDebrief() },
              onDismiss = { viewModel.dismissQuickDebrief() }
            )
          }
        }
      }
    }
  }
}
