package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GleoAiEngine
import com.example.audio.AudioCoachManager
import com.example.data.CctvRepository
import com.example.data.DrivingHabitHistoryRepository
import com.example.data.InsuranceRepository
import com.example.domain.DrivingEventFactory
import com.example.domain.TelematicsConstants
import com.example.service.DrivingTrackerService
import com.example.telematics.DrivingSensorFilter
import com.example.model.CameraMode
import com.example.model.CameraType
import com.example.model.ChatMessage
import com.example.model.CommuteChallenge
import com.example.model.DailyHabitRecord
import com.example.model.DriveSession
import com.example.model.DrivingEvent
import com.example.model.DrivingEventType
import com.example.model.DrivingHabitMetric
import com.example.model.DrivingHabitTrackerData
import com.example.model.EnforcementCamera
import com.example.model.EventSeverity
import com.example.model.InsuranceCompanyQuote
import com.example.model.IssuedCoupon
import com.example.model.LaneDetectionState
import com.example.model.LineType
import com.example.model.ParkingFeeData
import com.example.model.ParkingGracePeriodState
import com.example.model.ParkingMemoryData
import com.example.model.QuickDebriefData
import com.example.model.TurnSignal
import com.example.model.VehicleConsumable
import com.example.model.VehicleGpsCoordinate
import com.example.model.WeeklyDrivingReportSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class CockpitUiState(
  val isDriving: Boolean = false,
  val currentSpeed: Int = 0,
  val speedLimit: Int = 60,
  val gForce: Float = 0.0f,
  val tripDistanceKm: Float = 0.0f,
  val tripDurationSeconds: Long = 0L,
  val currentTripScore: Int = 100,
  val isSeatbeltFastened: Boolean = true,
  val turnSignal: TurnSignal = TurnSignal.OFF,
  val activeBannerMessage: DrivingEvent? = null,
  val recentEvents: List<DrivingEvent> = emptyList(),
  val nearestCctv: EnforcementCamera? = null,
  val vehicleCoordinate: VehicleGpsCoordinate = VehicleGpsCoordinate(),
  val isStationary: Boolean = true,
  val isVoiceMuted: Boolean = false,
  val laneState: LaneDetectionState = LaneDetectionState(),
  val habitTracker: DrivingHabitTrackerData = DrivingHabitTrackerData(),
  val lowHabitAlertMessage: String? = null,
  val accumulatedPoints: Int = 14250,
  val totalOdometerKm: Int = 42350,
  val vehicleModel: String = "제네시스 GV70 (12가 3456)",
  val liveFuelSavedWon: Int = 0,
  val liveAggressiveCostWon: Int = 0,
  val recentCostFloatingMessage: String? = null,
  val activeCalmCoachState: Boolean = false,
  val activeQuickDebrief: QuickDebriefData? = null,
  val isBeepOnlyCoachMode: Boolean = true
)

class SafeCoachViewModel(application: Application) : AndroidViewModel(application) {

  val audioCoach = AudioCoachManager(application.applicationContext)
  private val habitHistoryRepository = DrivingHabitHistoryRepository(application.applicationContext)
  val sensorFilter = DrivingSensorFilter()

  private val _cockpitState = MutableStateFlow(
    CockpitUiState(accumulatedPoints = habitHistoryRepository.loadAccumulatedPoints())
  )
  val cockpitState: StateFlow<CockpitUiState> = _cockpitState.asStateFlow()

  // Saved 7-Day Historical Driving Habit Tracker Data & Calculated Summary
  private val _weeklyHabitRecords = MutableStateFlow(habitHistoryRepository.loadWeeklyRecords())
  val weeklyHabitRecords: StateFlow<List<DailyHabitRecord>> = _weeklyHabitRecords.asStateFlow()

  private val _weeklyReportSummary = MutableStateFlow(WeeklyDrivingReportSummary(_weeklyHabitRecords.value))
  val weeklyReportSummary: StateFlow<WeeklyDrivingReportSummary> = _weeklyReportSummary.asStateFlow()

  // Weekly Report Data (Day to Summary Score pairs derived from 7-day records)
  private val _weeklyScoreHistory = MutableStateFlow(
    _weeklyHabitRecords.value.map { it.dayOfWeek to it.summaryScore }
  )
  val weeklyScoreHistory = _weeklyScoreHistory.asStateFlow()

  private val _recentTrips = MutableStateFlow(habitHistoryRepository.loadRecentTrips())
  val recentTrips = _recentTrips.asStateFlow()

  private val cctvRepository = CctvRepository()
  private val insuranceRepository = InsuranceRepository()
  private val gleoAiEngine = GleoAiEngine()

  // Surrounding CCTV / Speed Cameras from CctvRepository (SRP)
  private val _cctvCameras = MutableStateFlow(cctvRepository.getDefaultCameras())
  val cctvCameras = _cctvCameras.asStateFlow()

  // Insurance Discounts from InsuranceRepository (SRP)
  private val _insuranceQuotes = MutableStateFlow(insuranceRepository.getInsuranceQuotes())
  val insuranceQuotes = _insuranceQuotes.asStateFlow()

  // Vehicle Consumables
  private val _consumables = MutableStateFlow(habitHistoryRepository.loadConsumables())
  val consumables = _consumables.asStateFlow()

  // Gleo AI Chat Messages
  private val _chatMessages = MutableStateFlow(habitHistoryRepository.loadChatMessages())
  val chatMessages = _chatMessages.asStateFlow()

  // Feature 4: Parking Grace Period Tracker ("골든타임" 카운트다운)
  private val _parkingGraceState = MutableStateFlow(
    ParkingGracePeriodState(
      isTracking = true,
      cameraName = "선릉역 교차로 주정차 단속 (선릉로)",
      graceTotalSeconds = 600,
      remainingSeconds = 590,
      isLunchTimeGrace = isCurrentTimeLunchGrace()
    )
  )
  val parkingGraceState: StateFlow<ParkingGracePeriodState> = _parkingGraceState.asStateFlow()

  // Feature 2: Micro-Rewards & Commute Safety Challenges (출퇴근 안전 챌린지)
  private val _commuteChallenges = MutableStateFlow(insuranceRepository.getDefaultCommuteChallenges())
  val commuteChallenges: StateFlow<List<CommuteChallenge>> = _commuteChallenges.asStateFlow()

  // My Coupon Wallet
  private val _issuedCoupons = MutableStateFlow(insuranceRepository.getInitialIssuedCoupons())
  val issuedCoupons: StateFlow<List<IssuedCoupon>> = _issuedCoupons.asStateFlow()

  // Feature 6: Smart Parking Memory & Door-Ding Defense (주차장 기억 & 문콕 방어)
  private val _parkingMemory = MutableStateFlow(ParkingMemoryData())
  val parkingMemory: StateFlow<ParkingMemoryData> = _parkingMemory.asStateFlow()

  // Feature 7: Parking Fee Signboard 1-Second Translator (주차 요금 표지판 1초 번역기)
  private val _parkingFeeData = MutableStateFlow(ParkingFeeData())
  val parkingFeeData: StateFlow<ParkingFeeData> = _parkingFeeData.asStateFlow()

  fun scanParkingPillar() {
    val sdf = SimpleDateFormat("오늘 HH:mm 주차", Locale.KOREA)
    _parkingMemory.update {
      it.copy(
        parkingTime = sdf.format(Date()),
        isScanned = true
      )
    }
    audioCoach.speakBriefing(
      "카메라로 지하 3층 D-12 기둥 위치를 기억했어요. 기둥 쪽에 바짝 주차해서 문콕 걱정 없어요!"
    )
  }

  fun selectParkingHours(hours: Int) {
    _parkingFeeData.update { it.copy(selectedHours = hours) }
    val info = _parkingFeeData.value
    audioCoach.speakBriefing(info.summaryBriefing)
  }

  fun speakParkingFeeBriefing() {
    audioCoach.speakBriefing(_parkingFeeData.value.summaryBriefing)
  }

  private var driveJob: Job? = null
  private var parkingGraceJob: Job? = null

  init {
    // Initial camera
    _cockpitState.update { it.copy(nearestCctv = _cctvCameras.value.firstOrNull()) }
    startParkingGracePeriodLoop()
  }

  private fun isCurrentTimeLunchGrace(): Boolean {
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    return (hour == 11 && minute >= 30) || (hour in 12..13) || (hour == 14 && minute == 0)
  }

  private fun startParkingGracePeriodLoop() {
    parkingGraceJob?.cancel()
    parkingGraceJob = viewModelScope.launch {
      while (true) {
        delay(1000)
        if (_parkingGraceState.value.isTracking) {
          val currentSec = _parkingGraceState.value.remainingSeconds
          if (currentSec > 0) {
            val nextSec = currentSec - 1
            val shouldWarn = (nextSec == TelematicsConstants.PARKING_GRACE_5MIN_WARNING_SECONDS && !_parkingGraceState.value.hasWarned5Min)
            _parkingGraceState.update {
              it.copy(
                remainingSeconds = nextSec,
                hasWarned5Min = it.hasWarned5Min || shouldWarn
              )
            }
            if (shouldWarn) {
              audioCoach.speakBriefing(
                "주정차 단속 유예 시간이 5분 남았습니다. 과태료 부과를 방지하기 위해 차를 안전한 곳으로 이동해 주세요.",
                vibrate = true
              )
            }
          } else {
            if (!_parkingGraceState.value.isExpired) {
              _parkingGraceState.update { it.copy(isExpired = true) }
              audioCoach.speakBriefing(
                "주정차 단속 유예 시간이 만료되었습니다. 즉시 차량을 이동하세요!",
                vibrate = true
              )
            }
          }
        }
      }
    }
  }

  fun moveVehicleComplete() {
    _parkingGraceState.update { it.copy(isTracking = false, remainingSeconds = TelematicsConstants.PARKING_GRACE_DEFAULT_SECONDS, isExpired = false) }
    audioCoach.speak("차량 이동이 확인되었습니다. 안전하게 주차가 완료되었습니다.")
  }

  fun resetParkingGraceTimer() {
    _parkingGraceState.update {
      it.copy(
        isTracking = true,
        remainingSeconds = TelematicsConstants.PARKING_GRACE_DEFAULT_SECONDS,
        isExpired = false,
        hasWarned5Min = false
      )
    }
    audioCoach.speak("주정차 단속 10분 골든타임 타이머를 다시 시작합니다.")
  }

  fun simulate5MinParkingWarning() {
    _parkingGraceState.update {
      it.copy(
        isTracking = true,
        remainingSeconds = TelematicsConstants.PARKING_GRACE_5MIN_WARNING_SECONDS,
        hasWarned5Min = true,
        isExpired = false
      )
    }
    audioCoach.speakBriefing(
      "주정차 단속 유예 시간이 5분 남았습니다. 과태료 부과를 방지하기 위해 차를 안전한 곳으로 이동해 주세요.",
      vibrate = true
    )
  }

  fun claimChallengeCoupon(challengeId: String) {
    val challenge = _commuteChallenges.value.find { it.id == challengeId } ?: return
    if (challenge.isClaimed || !challenge.isCompleted) return

    val newCoupon = IssuedCoupon(
      id = UUID.randomUUID().toString(),
      title = challenge.rewardTitle,
      brand = challenge.rewardTitle.split(" ").firstOrNull() ?: "카포켓",
      barcodeNumber = "880" + (1000000..9999999).random().toString(),
      expiryDate = "2026.12.31까지",
      valueLabel = challenge.rewardTag,
      iconEmoji = challenge.iconEmoji,
      isUsed = false
    )

    _commuteChallenges.update { list ->
      list.map { if (it.id == challengeId) it.copy(isClaimed = true) else it }
    }
    _issuedCoupons.update { listOf(newCoupon) + it }
    audioCoach.speakBriefing("축하합니다! ${challenge.rewardTitle}이 쿠폰함에 지급되었어요. 바로 사용해 보세요!")
  }

  fun useIssuedCoupon(couponId: String) {
    _issuedCoupons.update { list ->
      list.map { if (it.id == couponId) it.copy(isUsed = true) else it }
    }
    audioCoach.speak("쿠폰 사용이 확인되었습니다. 유용하게 사용하셨기를 바라요!")
  }

  fun selectCctvCamera(camera: EnforcementCamera, speakAlert: Boolean = true) {
    _cockpitState.update {
      it.copy(
        nearestCctv = camera,
        speedLimit = if (camera.speedLimit > 0) camera.speedLimit else it.speedLimit
      )
    }
    if (speakAlert) {
      val speedText = if (camera.speedLimit > 0) "시속 ${camera.speedLimit}km 제한 " else ""
      audioCoach.speak(
        "단속 정보 안내: ${camera.title}, 전방 ${camera.distanceMeters}미터 ${speedText}${camera.type.label} 구간입니다. ${camera.activeNotice}"
      )
    }
  }

  fun toggleVoiceMute() {
    val newMute = !_cockpitState.value.isVoiceMuted
    audioCoach.isVoiceEnabled = !newMute
    _cockpitState.update { it.copy(isVoiceMuted = newMute) }
    if (!newMute) {
      audioCoach.speak("음성 코칭이 활성화되었습니다.")
    } else {
      audioCoach.stop()
    }
  }

  fun toggleSeatbelt() {
    val newSeatbelt = !_cockpitState.value.isSeatbeltFastened
    _cockpitState.update { it.copy(isSeatbeltFastened = newSeatbelt) }
    if (!newSeatbelt) {
      triggerDrivingEvent(DrivingEventType.UNBUCKLED_SEATBELT)
    } else {
      audioCoach.speak("안전벨트 착용이 확인되었습니다. 안전 운행을 시작합니다.")
    }
  }

  fun setTurnSignal(signal: TurnSignal) {
    _cockpitState.update {
      it.copy(turnSignal = if (it.turnSignal == signal) TurnSignal.OFF else signal)
    }
  }

  fun toggleCameraMode() {
    val currentMode = _cockpitState.value.laneState.currentCameraMode
    val nextMode = if (currentMode == CameraMode.FRONT) CameraMode.REAR else CameraMode.FRONT
    _cockpitState.update {
      it.copy(laneState = it.laneState.copy(currentCameraMode = nextMode))
    }
    audioCoach.speak("${nextMode.label}로 전환되었습니다.")
  }

  fun toggleLaneSolidType() {
    val currentType = _cockpitState.value.laneState.rightLineType
    val nextType = if (currentType == LineType.SOLID) LineType.DASHED else LineType.SOLID
    _cockpitState.update {
      it.copy(laneState = it.laneState.copy(rightLineType = nextType))
    }
    if (nextType == LineType.SOLID) {
      audioCoach.speak("주의: 우측 차로는 실선 구간입니다. 차선 변경이 금지됩니다.")
    } else {
      audioCoach.speak("점선 구간으로 진입했습니다. 안전 확인 후 차선 변경이 가능합니다.")
    }
  }

  fun toggleBeepOnlyCoachMode() {
    val nextMode = !_cockpitState.value.isBeepOnlyCoachMode
    audioCoach.isBeepOnlyMode = nextMode
    _cockpitState.update { it.copy(isBeepOnlyCoachMode = nextMode) }
    val msg = if (nextMode) "주행 중에는 간결한 비프음 위주로 안내하며, 도착 시 요약해 드려요." else "주행 중 상세 음성 코칭을 모두 안내해 드려요."
    audioCoach.speakBriefing(msg)
  }

  fun dismissQuickDebrief() {
    _cockpitState.update { it.copy(activeQuickDebrief = null) }
  }

  fun replayQuickDebrief() {
    _cockpitState.value.activeQuickDebrief?.let { debrief ->
      audioCoach.speakBriefing(debrief.speechText)
    }
  }

  fun triggerRoadRageCalmIntervention() {
    _cockpitState.update { it.copy(activeCalmCoachState = true) }
    val speech = gleoAiEngine.generateRoadRageCalmSpeech()
    audioCoach.speakBriefing(speech, vibrate = true)

    viewModelScope.launch {
      delay(TelematicsConstants.ROAD_RAGE_CALM_DISPLAY_DURATION_MS)
      _cockpitState.update { it.copy(activeCalmCoachState = false) }
    }
  }

  fun startDriving() {
    if (_cockpitState.value.isDriving) return
    _cockpitState.update {
      it.copy(
        isDriving = true,
        currentSpeed = 35,
        isStationary = false,
        tripDistanceKm = 0.1f,
        tripDurationSeconds = 0L,
        currentTripScore = 100,
        liveFuelSavedWon = 0,
        liveAggressiveCostWon = 0,
        recentCostFloatingMessage = null,
        activeQuickDebrief = null,
        activeCalmCoachState = false
      )
    }
    audioCoach.speakBriefing("카포켓 주행 분석을 시작합니다. 안전벨트를 확인하시고 규정 속도를 준수해 주세요.")
    DrivingTrackerService.startService(getApplication(), speed = 35, score = 100, distanceKm = 0.1f)

    driveJob?.cancel()
    driveJob = viewModelScope.launch {
      var speed = 42
      var distance = 0.1f
      var seconds = 0L
      var cctvDist = 320

      while (_cockpitState.value.isDriving) {
        delay(1000)
        seconds += 1
        distance += (speed / 3600f)

        // slight realistic variation with sensor noise filtering
        val speedVariation = (-2..3).random()
        val rawSpeed = (speed + speedVariation).coerceIn(30, 68).toFloat()
        val filteredSpeed = sensorFilter.filterGpsSpeed(rawSpeed, gpsAccuracyMeters = 5.0f).toInt()
        speed = filteredSpeed

        // cctv countdown
        cctvDist -= (speed * 0.28).toInt()
        if (cctvDist <= 30) {
          cctvDist = (400..800).random()
        }

        // Voice alert at 200m
        if (cctvDist in 180..210) {
          val cam = _cockpitState.value.nearestCctv
          if (cam != null) {
            audioCoach.speak("전방 200미터 시속 ${cam.speedLimit}km 단속 구간입니다. 현재 속도 ${speed}km입니다.")
          }
        }

        val nearest = _cockpitState.value.nearestCctv?.copy(distanceMeters = cctvDist)
        val fuelSaved = TelematicsConstants.calculateFuelSavingsWon(distance)

        _cockpitState.update {
          it.copy(
            currentSpeed = speed,
            speedLimit = 60,
            gForce = (speedVariation * 0.08f).coerceIn(-0.6f, 0.6f),
            tripDistanceKm = distance,
            tripDurationSeconds = seconds,
            nearestCctv = nearest,
            isStationary = (speed == 0),
            liveFuelSavedWon = fuelSaved
          )
        }

        DrivingTrackerService.updateStats(
          getApplication(),
          speed = speed,
          score = _cockpitState.value.currentTripScore,
          distanceKm = distance
        )
      }
    }
  }

  fun stopDriving() {
    if (!_cockpitState.value.isDriving) return
    driveJob?.cancel()
    driveJob = null
    DrivingTrackerService.stopService(getApplication())

    val finalScore = _cockpitState.value.currentTripScore
    val finalDist = _cockpitState.value.tripDistanceKm
    val earnedPoints = TelematicsConstants.calculateEarnedPoints(finalDist, finalScore)
    val fuelSaved = TelematicsConstants.calculateFuelSavingsWon(finalDist)

    // Build completed session
    val sdf = SimpleDateFormat("오늘 HH:mm 주행", Locale.KOREA)
    val newSession = DriveSession(
      id = UUID.randomUUID().toString(),
      dateLabel = sdf.format(Date()),
      timeLabel = "${_cockpitState.value.tripDurationSeconds / 60}분 소요",
      totalKm = ((_cockpitState.value.tripDistanceKm * 10).toInt() / 10f),
      durationMinutes = (_cockpitState.value.tripDurationSeconds / 60).toInt().coerceAtLeast(1),
      score = finalScore,
      rapidAccelCount = _cockpitState.value.recentEvents.count { it.type == DrivingEventType.RAPID_ACCEL },
      suddenBrakeCount = _cockpitState.value.recentEvents.count { it.type == DrivingEventType.SUDDEN_BRAKE },
      speedingCount = _cockpitState.value.recentEvents.count { it.type == DrivingEventType.SPEEDING },
      blinkerCompliancePercent = 95,
      fuelSavedWon = fuelSaved,
      topHighlight = "급가속 억제 및 부드러운 감속 우수",
      topImprovement = "단속 구간 진입 전 선제적 속도 제어 탁월"
    )

    // Generate 30-Second Quick Debrief
    val quickDebrief = gleoAiEngine.generateQuickDebrief(
      session = newSession,
      habitData = _cockpitState.value.habitTracker,
      fuelSavedWon = fuelSaved,
      earnedPoints = earnedPoints
    )

    _cockpitState.update {
      it.copy(
        isDriving = false,
        currentSpeed = 0,
        gForce = 0.0f,
        isStationary = true,
        accumulatedPoints = it.accumulatedPoints + earnedPoints,
        totalOdometerKm = it.totalOdometerKm + finalDist.toInt(),
        activeQuickDebrief = quickDebrief
      )
    }

    // Speak 30s Quick Debrief via Gleo AI (High Priority)
    audioCoach.speakBriefing(quickDebrief.speechText)

    _recentTrips.update {
      val updated = listOf(newSession) + it
      habitHistoryRepository.saveRecentTrips(updated)
      updated
    }
    habitHistoryRepository.saveAccumulatedPoints(_cockpitState.value.accumulatedPoints)
  }

  fun triggerDrivingEvent(type: DrivingEventType) {
    if (type == DrivingEventType.ROAD_RAGE_ALERT) {
      triggerRoadRageCalmIntervention()
      return
    }

    val speed = _cockpitState.value.currentSpeed.coerceAtLeast(45)
    val event = DrivingEventFactory.create(
      type = type,
      currentSpeedKmh = speed,
      voiceSpeechOverride = if (type == DrivingEventType.ROAD_RAGE_ALERT) gleoAiEngine.generateRoadRageCalmSpeech() else null
    )

    // Real currency loss calculation & floating message
    val costLossWon = when (type) {
      DrivingEventType.RAPID_ACCEL -> TelematicsConstants.RAPID_ACCEL_COST_WON
      DrivingEventType.SUDDEN_BRAKE -> TelematicsConstants.SUDDEN_BRAKE_COST_WON
      else -> 0
    }

    val floatingCostMsg = when (type) {
      DrivingEventType.RAPID_ACCEL -> "-180원 (급출발)"
      DrivingEventType.SUDDEN_BRAKE -> "-120원 (급제동)"
      else -> null
    }

    // Check road-rage chain reaction pattern (rapid accel immediately after sudden brake)
    val now = System.currentTimeMillis()
    if (type == DrivingEventType.RAPID_ACCEL && sensorFilter.evaluateRoadRageRisk(0.42f, now)) {
      triggerRoadRageCalmIntervention()
    }

    val updatedScore = (_cockpitState.value.currentTripScore - type.penaltyPoints).coerceAtLeast(30)
    val currentHabits = _cockpitState.value.habitTracker
    val updatedHabits = when (type) {
      DrivingEventType.SUDDEN_BRAKE, DrivingEventType.SAFE_DISTANCE_WARNING -> currentHabits.copy(
        smoothBrakingScore = (currentHabits.smoothBrakingScore - TelematicsConstants.HABIT_PENALTY_BRAKING).coerceAtLeast(TelematicsConstants.HABIT_MIN_FLOOR_SCORE),
        suddenBrakeCount = currentHabits.suddenBrakeCount + 1
      )
      DrivingEventType.RAPID_ACCEL, DrivingEventType.SPEEDING, DrivingEventType.SHARP_TURN -> currentHabits.copy(
        smoothAccelerationScore = (currentHabits.smoothAccelerationScore - TelematicsConstants.HABIT_PENALTY_ACCEL).coerceAtLeast(TelematicsConstants.HABIT_MIN_FLOOR_SCORE),
        rapidAccelCount = currentHabits.rapidAccelCount + 1
      )
      DrivingEventType.NO_BLINKER_LANE_CHANGE, DrivingEventType.SOLID_LANE_CROSS -> currentHabits.copy(
        indicatorUsageScore = (currentHabits.indicatorUsageScore - TelematicsConstants.HABIT_PENALTY_INDICATOR).coerceAtLeast(TelematicsConstants.HABIT_MIN_FLOOR_SCORE),
        missedIndicatorCount = currentHabits.missedIndicatorCount + 1
      )
      else -> currentHabits
    }

    val isBrakingOrAccelLow =
      updatedHabits.smoothBrakingScore < AudioCoachManager.LOW_HABIT_SCORE_THRESHOLD ||
        updatedHabits.smoothAccelerationScore < AudioCoachManager.LOW_HABIT_SCORE_THRESHOLD

    val lowScoreSpeech = if (isBrakingOrAccelLow) {
      audioCoach.evaluateAndSpeakLowHabitScore(updatedHabits, forceSpeak = false)
    } else {
      audioCoach.speak(event.voiceSpeech, vibrate = true)
      null
    }

    val bannerEvent = if (lowScoreSpeech != null && (lowScoreSpeech.startsWith("운전습관") || lowScoreSpeech.startsWith("위험"))) {
      event.copy(
        title = "${event.title} · 저점수 경고",
        voiceSpeech = lowScoreSpeech
      )
    } else {
      if (isBrakingOrAccelLow) {
        audioCoach.speak(event.voiceSpeech, vibrate = true)
      }
      event
    }

    _cockpitState.update {
      it.copy(
        activeBannerMessage = bannerEvent,
        recentEvents = listOf(bannerEvent) + it.recentEvents.take(9),
        currentTripScore = updatedScore,
        habitTracker = updatedHabits,
        lowHabitAlertMessage = lowScoreSpeech,
        liveAggressiveCostWon = it.liveAggressiveCostWon + costLossWon,
        recentCostFloatingMessage = floatingCostMsg
      )
    }
    persistHabitTrackerToHistory(updatedHabits)

    if (floatingCostMsg != null) {
      viewModelScope.launch {
        delay(TelematicsConstants.COST_FLOATING_MESSAGE_DURATION_MS)
        _cockpitState.update {
          if (it.recentCostFloatingMessage == floatingCostMsg) {
            it.copy(recentCostFloatingMessage = null)
          } else it
        }
      }
    }

    // Clear banner after duration
    viewModelScope.launch {
      delay(TelematicsConstants.BANNER_MESSAGE_DURATION_MS)
      _cockpitState.update { current ->
        if (current.activeBannerMessage?.id == bannerEvent.id) {
          current.copy(activeBannerMessage = null)
        } else current
      }
    }
  }

  fun triggerLowBrakingHabitAlert(targetScore: Int = 72) {
    val currentHabits = _cockpitState.value.habitTracker
    val updatedHabits = currentHabits.copy(
      smoothBrakingScore = targetScore,
      suddenBrakeCount = (currentHabits.suddenBrakeCount + 2).coerceAtLeast(2)
    )
    val alertSpeech = audioCoach.speakLowBrakingScoreAlert(targetScore)
    val alertEvent = DrivingEvent(
      id = UUID.randomUUID().toString(),
      type = DrivingEventType.SUDDEN_BRAKE,
      title = "제동 저점수 감지 (${targetScore}점)",
      message = "부드러운 제동 점수가 안전 기준(80점) 미만으로 하락했습니다.",
      voiceSpeech = alertSpeech,
      timestamp = System.currentTimeMillis(),
      speedKmh = _cockpitState.value.currentSpeed.coerceAtLeast(45),
      severity = EventSeverity.CRITICAL
    )

    _cockpitState.update {
      it.copy(
        habitTracker = updatedHabits,
        lowHabitAlertMessage = alertSpeech,
        activeBannerMessage = alertEvent,
        currentTripScore = updatedHabits.averageScore
      )
    }
    persistHabitTrackerToHistory(updatedHabits)
  }

  fun triggerLowAccelerationHabitAlert(targetScore: Int = 74) {
    val currentHabits = _cockpitState.value.habitTracker
    val updatedHabits = currentHabits.copy(
      smoothAccelerationScore = targetScore,
      rapidAccelCount = (currentHabits.rapidAccelCount + 2).coerceAtLeast(2)
    )
    val alertSpeech = audioCoach.speakLowAccelerationScoreAlert(targetScore)
    val alertEvent = DrivingEvent(
      id = UUID.randomUUID().toString(),
      type = DrivingEventType.RAPID_ACCEL,
      title = "가속 저점수 감지 (${targetScore}점)",
      message = "부드러운 가속 점수가 안전 기준(80점) 미만으로 하락했습니다.",
      voiceSpeech = alertSpeech,
      timestamp = System.currentTimeMillis(),
      speedKmh = _cockpitState.value.currentSpeed.coerceAtLeast(50),
      severity = EventSeverity.WARNING
    )

    _cockpitState.update {
      it.copy(
        habitTracker = updatedHabits,
        lowHabitAlertMessage = alertSpeech,
        activeBannerMessage = alertEvent,
        currentTripScore = updatedHabits.averageScore
      )
    }
    persistHabitTrackerToHistory(updatedHabits)
  }

  fun replayLowHabitScoreAlert() {
    val habits = _cockpitState.value.habitTracker
    val spoken = audioCoach.evaluateAndSpeakLowHabitScore(habits, forceSpeak = true)
    if (spoken != null) {
      _cockpitState.update { it.copy(lowHabitAlertMessage = spoken) }
    }
  }

  fun speakHabitCoachingTip(metric: DrivingHabitMetric) {
    if (metric.category == com.example.model.HabitCategory.SMOOTH_BRAKING && metric.score < AudioCoachManager.LOW_HABIT_SCORE_THRESHOLD) {
      audioCoach.speakLowBrakingScoreAlert(metric.score)
    } else if (metric.category == com.example.model.HabitCategory.SMOOTH_ACCELERATION && metric.score < AudioCoachManager.LOW_HABIT_SCORE_THRESHOLD) {
      audioCoach.speakLowAccelerationScoreAlert(metric.score)
    } else {
      audioCoach.speak("${metric.titleKo} 현재 점수는 ${metric.score}점입니다. ${metric.coachingTip}", vibrate = false)
    }
  }

  fun resetHabitScores() {
    audioCoach.resetAlertBands()
    val resetHabits = DrivingHabitTrackerData()
    _cockpitState.update {
      it.copy(
        habitTracker = resetHabits,
        lowHabitAlertMessage = null,
        currentTripScore = 100
      )
    }
    val resetRecords = habitHistoryRepository.resetToDefaultRecords()
    updateWeeklyState(resetRecords)
    audioCoach.speak("운전습관 점수를 처음부터 다시 잴 수 있게 준비했어요. 안전하게 달려보세요!", vibrate = false)
  }

  fun syncTodayHabitToWeeklyReport() {
    val currentHabits = _cockpitState.value.habitTracker
    persistHabitTrackerToHistory(currentHabits)
    val summary = _weeklyReportSummary.value
    audioCoach.speak(
      "오늘 운전 기록을 주간 리포트에 쏙 담았어요. 최근 7일 종합 점수는 ${summary.overallSummaryScore}점이에요.",
      vibrate = false
    )
  }

  fun speakWeeklyReportSummary() {
    val summary = _weeklyReportSummary.value
    val deltaText = if (summary.overallTrendDelta >= 0) "${summary.overallTrendDelta}점 올랐" else "${-summary.overallTrendDelta}점 내려갔"
    val speech =
      "최근 7일 운전 리포트 요약이에요. 종합 안전점수는 ${summary.overallSummaryScore}점으로 지난주보다 $deltaText 어요. " +
        "부드럽게 멈추기 평균 ${summary.avgSmoothBraking}점, 부드럽게 출발하기 평균 ${summary.avgSmoothAcceleration}점, 깜빡이 켜기 평균 ${summary.avgIndicatorUsage}점이에요."
    audioCoach.speak(speech, vibrate = false)
  }

  private fun persistHabitTrackerToHistory(habitData: DrivingHabitTrackerData, additionalKm: Float = 0f) {
    val updatedRecords = habitHistoryRepository.updateTodayRecordWithHabitTracker(
      currentRecords = _weeklyHabitRecords.value,
      habitTracker = habitData,
      additionalDistanceKm = additionalKm
    )
    updateWeeklyState(updatedRecords)
  }

  private fun updateWeeklyState(records: List<DailyHabitRecord>) {
    _weeklyHabitRecords.value = records
    _weeklyReportSummary.value = WeeklyDrivingReportSummary(records)
    _weeklyScoreHistory.value = records.map { it.dayOfWeek to it.summaryScore }
  }

  fun triggerLaneDepartureAlert(side: String) {
    _cockpitState.update {
      it.copy(
        laneState = it.laneState.copy(
          laneDepartureWarning = true,
          departureSide = side
        )
      )
    }
    audioCoach.speak("차로를 이탈하고 있습니다. 핸들을 중앙으로 유지해 주세요.", vibrate = true)
    viewModelScope.launch {
      delay(TelematicsConstants.LANE_DEPARTURE_WARNING_DURATION_MS)
      _cockpitState.update {
        it.copy(laneState = it.laneState.copy(laneDepartureWarning = false, departureSide = ""))
      }
    }
  }

  fun replaceConsumable(id: String) {
    _consumables.update { list ->
      val updated = list.map { item ->
        if (item.id == id) {
          item.copy(
            currentKm = 0,
            healthPercent = 100,
            dDayDays = 180,
            isUrgent = false
          )
        } else item
      }
      habitHistoryRepository.saveConsumables(updated)
      updated
    }
    audioCoach.speak("소모품 교체가 완료되었어요. 다음 점검 때까지 꼼꼼히 챙겨드릴게요.")
  }

  fun exchangeReward(pointsCost: Int, rewardName: String): Boolean {
    if (_cockpitState.value.accumulatedPoints < pointsCost) {
      audioCoach.speak("포인트가 조금 부족해요. 안전운전으로 포인트를 더 모아보세요!")
      return false
    }
    _cockpitState.update {
      val next = it.copy(accumulatedPoints = it.accumulatedPoints - pointsCost)
      habitHistoryRepository.saveAccumulatedPoints(next.accumulatedPoints)
      next
    }
    audioCoach.speak("${rewardName} 교환이 끝났어요. 내 쿠폰함에서 바로 확인해 보세요!")
    return true
  }

  // Gleo AI Chat Interaction
  fun sendGleoMessage(query: String) {
    if (query.isBlank()) return

    val userMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      text = query.trim(),
      isUser = true
    )

    _chatMessages.update {
      val updated = it + userMsg
      habitHistoryRepository.saveChatMessages(updated)
      updated
    }

    viewModelScope.launch {
      delay(600) // thinking delay
      val responseText = gleoAiEngine.generateAnswer(
        query = query,
        currentScore = _cockpitState.value.currentTripScore,
        totalOdometerKm = _cockpitState.value.totalOdometerKm,
        nearestCctv = _cockpitState.value.nearestCctv
      )
      val gleoMsg = ChatMessage(
        id = UUID.randomUUID().toString(),
        text = responseText,
        isUser = false,
        spokenByTts = true
      )
      _chatMessages.update {
        val updated = it + gleoMsg
        habitHistoryRepository.saveChatMessages(updated)
        updated
      }
      audioCoach.speak(responseText)
    }
  }

  override fun onCleared() {
    super.onCleared()
    driveJob?.cancel()
    parkingGraceJob?.cancel()
    audioCoach.shutdown()
  }
}
