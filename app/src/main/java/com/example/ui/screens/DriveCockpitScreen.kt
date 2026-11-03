package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.TelematicsConstants
import com.example.model.DrivingEventType
import com.example.model.QuickDebriefData
import com.example.model.TurnSignal
import com.example.ui.components.CarPocketCoinVaultIcon
import com.example.ui.components.CctvEnforcementMapView
import com.example.ui.components.CctvRadarCard
import com.example.ui.components.DrivingHabitTrackerCard
import com.example.ui.components.LaneDetectionHud
import com.example.ui.components.ParkingGraceTimerCard
import com.example.ui.components.QuickDebriefCardOverlay
import com.example.ui.components.RoadRageCalmOverlay
import com.example.ui.components.SpeedometerGauge
import com.example.ui.components.TurnSignalSeatbeltIndicator
import com.example.ui.components.VoiceCoachBanner
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.GoldStar
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import com.example.util.toFormattedKm
import com.example.util.toFormattedPoints
import com.example.viewmodel.SafeCoachViewModel

@Composable
fun DriveCockpitScreen(
  viewModel: SafeCoachViewModel,
  onOpenCctvList: () -> Unit,
  modifier: Modifier = Modifier
) {
  val state by viewModel.cockpitState.collectAsState()
  val cctvCameras by viewModel.cctvCameras.collectAsState()

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
            SpeedometerGauge(
              speed = state.currentSpeed,
              speedLimit = state.speedLimit,
              gForce = state.gForce
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
            TurnSignalSeatbeltIndicator(
              turnSignal = state.turnSignal,
              isSeatbeltFastened = state.isSeatbeltFastened,
              onLeftSignalClick = { viewModel.setTurnSignal(TurnSignal.LEFT) },
              onRightSignalClick = { viewModel.setTurnSignal(TurnSignal.RIGHT) },
              onSeatbeltToggle = { viewModel.toggleSeatbelt() }
            )
          }
        }

        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            DrivingHabitTrackerCard(
              habitData = state.habitTracker,
              lowHabitAlertMessage = state.lowHabitAlertMessage,
              onMetricClick = { viewModel.speakHabitCoachingTip(it) },
              onTriggerLowBrakingAlert = { viewModel.triggerLowBrakingHabitAlert() },
              onTriggerLowAccelAlert = { viewModel.triggerLowAccelerationHabitAlert() },
              onReplayLowScoreAlert = { viewModel.replayLowHabitScoreAlert() },
              onResetClick = { viewModel.resetHabitScores() }
            )
          }
          item {
            CctvEnforcementMapView(
              cameras = cctvCameras,
              selectedCamera = state.nearestCctv,
              vehicleCoordinate = state.vehicleCoordinate,
              isStationary = state.isStationary,
              onSelectCamera = { viewModel.selectCctvCamera(it) },
              onOpenFullList = onOpenCctvList
            )
          }
          item {
            val graceState by viewModel.parkingGraceState.collectAsState()
            if (graceState.isTracking) {
              ParkingGraceTimerCard(
                graceState = graceState,
                onMoveVehicleComplete = { viewModel.moveVehicleComplete() },
                onResetTimer = { viewModel.resetParkingGraceTimer() },
                onSimulate5MinWarning = { viewModel.simulate5MinParkingWarning() }
              )
            }
          }
          item {
            LaneDetectionHud(
              laneState = state.laneState,
              onToggleCamera = { viewModel.toggleCameraMode() },
              onToggleLaneType = { viewModel.toggleLaneSolidType() },
              onTriggerDeparture = { side -> viewModel.triggerLaneDepartureAlert(side) }
            )
          }
          item {
            SimulationTestLabCard(
              onTrigger = { viewModel.triggerDrivingEvent(it) },
              onTriggerRoadRage = { viewModel.triggerRoadRageCalmIntervention() }
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
          SpeedometerGauge(
            speed = state.currentSpeed,
            speedLimit = state.speedLimit,
            gForce = state.gForce
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
          TurnSignalSeatbeltIndicator(
            turnSignal = state.turnSignal,
            isSeatbeltFastened = state.isSeatbeltFastened,
            onLeftSignalClick = { viewModel.setTurnSignal(TurnSignal.LEFT) },
            onRightSignalClick = { viewModel.setTurnSignal(TurnSignal.RIGHT) },
            onSeatbeltToggle = { viewModel.toggleSeatbelt() }
          )
        }
        item {
          DrivingHabitTrackerCard(
            habitData = state.habitTracker,
            lowHabitAlertMessage = state.lowHabitAlertMessage,
            onMetricClick = { viewModel.speakHabitCoachingTip(it) },
            onTriggerLowBrakingAlert = { viewModel.triggerLowBrakingHabitAlert() },
            onTriggerLowAccelAlert = { viewModel.triggerLowAccelerationHabitAlert() },
            onReplayLowScoreAlert = { viewModel.replayLowHabitScoreAlert() },
            onResetClick = { viewModel.resetHabitScores() }
          )
        }
        item {
          CctvEnforcementMapView(
            cameras = cctvCameras,
            selectedCamera = state.nearestCctv,
            vehicleCoordinate = state.vehicleCoordinate,
            isStationary = state.isStationary,
            onSelectCamera = { viewModel.selectCctvCamera(it) },
            onOpenFullList = onOpenCctvList
          )
        }
        item {
          val graceState by viewModel.parkingGraceState.collectAsState()
          if (graceState.isTracking) {
            ParkingGraceTimerCard(
              graceState = graceState,
              onMoveVehicleComplete = { viewModel.moveVehicleComplete() },
              onResetTimer = { viewModel.resetParkingGraceTimer() },
              onSimulate5MinWarning = { viewModel.simulate5MinParkingWarning() }
            )
          }
        }
        item {
          LaneDetectionHud(
            laneState = state.laneState,
            onToggleCamera = { viewModel.toggleCameraMode() },
            onToggleLaneType = { viewModel.toggleLaneSolidType() },
            onTriggerDeparture = { side -> viewModel.triggerLaneDepartureAlert(side) }
          )
        }
        item {
          SimulationTestLabCard(
            onTrigger = { viewModel.triggerDrivingEvent(it) },
            onTriggerRoadRage = { viewModel.triggerRoadRageCalmIntervention() }
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

@Composable
private fun CockpitStatusHeader(
  state: com.example.viewmodel.CockpitUiState,
  onToggleMute: () -> Unit,
  onToggleBeepMode: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      // 1행: 가변 제목 ── 고정 수치 뱃지 + 비프음 모드 + 음성 버튼
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = state.vehicleModel,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          modifier = Modifier.weight(1f, fill = false),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Neutral capsule score pill (softWrap = false)
          Box(
            modifier = Modifier
              .height(30.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(LightSurfaceSubtle)
              .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "운전점수 ",
                fontSize = 11.sp,
                color = TextSecondary,
                softWrap = false,
                maxLines = 1,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
              Text(
                text = "${state.currentTripScore}점",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.currentTripScore >= 80) EmeraldSafe else CrimsonAlert,
                fontFamily = FontFamily.Monospace,
                softWrap = false,
                maxLines = 1,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }

          // Beep-only Mode Toggle Pill (Single Action, No Duplicate Mute Button)
          Box(
            modifier = Modifier
              .height(30.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (state.isBeepOnlyCoachMode) CyanGlow else LightSurfaceSubtle)
              .border(
                1.dp,
                if (state.isBeepOnlyCoachMode) CyanNeon.copy(alpha = 0.5f) else LightCardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { onToggleBeepMode() }
              .padding(horizontal = 8.dp)
              .testTag("beep_mode_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "비프음 간소화",
                tint = if (state.isBeepOnlyCoachMode) CyanNeon else TextMuted,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (state.isBeepOnlyCoachMode) "비프음 모드" else "음성 안내",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.isBeepOnlyCoachMode) CyanNeon else TextSecondary,
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

      Spacer(modifier = Modifier.height(6.dp))

      // 2행: 메타 상태 인디케이터
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(if (state.isDriving) EmeraldSafe else TextMuted)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (state.isDriving) "실시간 안전 코칭 중" else "출발 준비 완료",
          fontSize = 11.sp,
          color = if (state.isDriving) EmeraldSafe else TextSecondary,
          fontWeight = FontWeight.Medium,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }
    }
  }
}

@Composable
private fun FuelEconomyCurrencyBadge(
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
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isDriving) "기름값 아끼는 중" else "주행 대기",
                fontSize = 10.sp,
                color = if (isDriving) EmeraldSafe else TextMuted,
                maxLines = 1,
                softWrap = false,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
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
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
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
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
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
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
              if (liveAggressiveCostWon > 0) {
                Text(
                  text = "-${liveAggressiveCostWon}원",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = CrimsonAlert,
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
  }
}

@Composable
private fun LiveDriveMetricsRow(state: com.example.viewmodel.CockpitUiState) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    MetricTile(
      label = "달린 거리",
      value = state.tripDistanceKm.toFormattedKm(),
      valueColor = TextPrimary,
      modifier = Modifier.weight(1f)
    )

    val mins = state.tripDurationSeconds / 60
    val secs = state.tripDurationSeconds % 60
    MetricTile(
      label = "운전 시간",
      value = String.format("%02d:%02d", mins, secs),
      valueColor = TextPrimary,
      modifier = Modifier.weight(1f)
    )

    MetricTile(
      label = "쌓인 포인트",
      value = state.accumulatedPoints.toFormattedPoints(),
      valueColor = CyanNeon,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun MetricTile(
  label: String,
  value: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
      Text(
        text = label,
        fontSize = 11.sp,
        color = TextMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = valueColor,
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )
    }
  }
}

@Composable
private fun MasterDriveActionButton(
  isDriving: Boolean,
  onToggleDrive: () -> Unit
) {
  Button(
    onClick = onToggleDrive,
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .testTag("drive_action_button"),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isDriving) CrimsonAlert else CyanNeon,
      contentColor = Color.White
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
  ) {
    Icon(
      imageVector = if (isDriving) Icons.Default.Stop else Icons.Default.PlayArrow,
      contentDescription = null,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = if (isDriving) "운전 마치기" else "안전운전 코칭 시작",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      style = TextStyle(
        platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
      )
    )
  }
}

@Composable
private fun SimulationTestLabCard(
  onTrigger: (DrivingEventType) -> Unit,
  onTriggerRoadRage: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { isExpanded = !isExpanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            text = "기능 체험 테스트 랩",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            style = NoFontPaddingTextStyle
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (isExpanded) "탭하여 접기" else "급조작·연비·감정케어 바로 체험하기",
            fontSize = 10.sp,
            color = TextMuted,
            style = NoFontPaddingTextStyle
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (isExpanded) "접기" else "체험하기",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            style = NoFontPaddingTextStyle
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column {
          Spacer(modifier = Modifier.height(12.dp))

          // 1행: 급조작 & 비용 소모
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            EventTriggerChip(
              title = "급출발 (-180원)",
              color = AmberWarning,
              bgColor = AmberGlow,
              onClick = { onTrigger(DrivingEventType.RAPID_ACCEL) },
              modifier = Modifier.weight(1f)
            )
            EventTriggerChip(
              title = "급제동 (-120원)",
              color = CrimsonAlert,
              bgColor = CrimsonGlow,
              onClick = { onTrigger(DrivingEventType.SUDDEN_BRAKE) },
              modifier = Modifier.weight(1f)
            )
            EventTriggerChip(
              title = "과속 경고",
              color = CrimsonAlert,
              bgColor = CrimsonGlow,
              onClick = { onTrigger(DrivingEventType.SPEEDING) },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 2행: 주행 법규 및 안전거리
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            EventTriggerChip(
              title = "깜빡이 미점등",
              color = AmberWarning,
              bgColor = AmberGlow,
              onClick = { onTrigger(DrivingEventType.NO_BLINKER_LANE_CHANGE) },
              modifier = Modifier.weight(1f)
            )
            EventTriggerChip(
              title = "실선 변경",
              color = CrimsonAlert,
              bgColor = CrimsonGlow,
              onClick = { onTrigger(DrivingEventType.SOLID_LANE_CROSS) },
              modifier = Modifier.weight(1f)
            )
            EventTriggerChip(
              title = "안전거리 경고",
              color = AmberWarning,
              bgColor = AmberGlow,
              onClick = { onTrigger(DrivingEventType.SAFE_DISTANCE_WARNING) },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 3행: Gleo 실시간 감정 케어 & 보복운전 예방
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            EventTriggerChip(
              title = "🧘 3초 진정 코칭 (보복 예방)",
              color = CyanNeon,
              bgColor = CyanGlow,
              onClick = onTriggerRoadRage,
              modifier = Modifier.weight(1.3f)
            )
            EventTriggerChip(
              title = "🚨 보복운전 연쇄 감지",
              color = CrimsonAlert,
              bgColor = CrimsonGlow,
              onClick = { onTrigger(DrivingEventType.ROAD_RAGE_ALERT) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}

@Composable
fun EventTriggerChip(
  title: String,
  color: Color,
  bgColor: Color = LightSurfaceSubtle,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .defaultMinSize(minHeight = 36.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor.copy(alpha = 0.65f))
      .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 4.dp, vertical = 7.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = color,
      softWrap = false,
      maxLines = 1,
      style = TextStyle(
        platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
      )
    )
  }
}
