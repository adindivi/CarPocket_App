package com.example.model

enum class DrivingEventType(
  val label: String,
  val iconName: String,
  val penaltyPoints: Int
) {
  RAPID_ACCEL("급가속", "speed", 5),
  SUDDEN_BRAKE("급제동", "brake", 7),
  SHARP_TURN("급회전", "turn", 5),
  SPEEDING("과속", "warning", 8),
  UNBUCKLED_SEATBELT("안전벨트 미착용", "seatbelt", 10),
  NO_BLINKER_LANE_CHANGE("깜빡이 미점등 차선변경", "blinker", 6),
  SOLID_LANE_CROSS("실선 차선변경 금지 위반", "lane", 8),
  SAFE_DISTANCE_WARNING("안전거리 미확보", "distance", 4),
  ROAD_RAGE_ALERT("운전 감정 케어", "calm", 0)
}

enum class EventSeverity {
  INFO, WARNING, CRITICAL
}

data class DrivingEvent(
  val id: String,
  val type: DrivingEventType,
  val title: String,
  val message: String,
  val voiceSpeech: String,
  val timestamp: Long,
  val speedKmh: Int,
  val severity: EventSeverity
)

enum class HabitCategory {
  SMOOTH_BRAKING,
  SMOOTH_ACCELERATION,
  INDICATOR_USAGE
}

data class DrivingHabitMetric(
  val category: HabitCategory,
  val titleKo: String,
  val titleEn: String,
  val score: Int,
  val targetScore: Int = 80,
  val violationCount: Int = 0,
  val weeklyDelta: Int = 0,
  val statusComment: String,
  val coachingTip: String
)

data class DrivingHabitTrackerData(
  val smoothBrakingScore: Int = 94,
  val smoothAccelerationScore: Int = 98,
  val indicatorUsageScore: Int = 95,
  val suddenBrakeCount: Int = 0,
  val rapidAccelCount: Int = 0,
  val missedIndicatorCount: Int = 0,
  val isLiveTracking: Boolean = true
) {
  val averageScore: Int
    get() = (smoothBrakingScore + smoothAccelerationScore + indicatorUsageScore) / 3

  fun toMetricList(): List<DrivingHabitMetric> = listOf(
    DrivingHabitMetric(
      category = HabitCategory.SMOOTH_BRAKING,
      titleKo = "부드럽게 멈추기",
      titleEn = "Smooth Braking",
      score = smoothBrakingScore.coerceIn(0, 100),
      targetScore = 80,
      violationCount = suddenBrakeCount,
      weeklyDelta = +4,
      statusComment = if (suddenBrakeCount == 0) "급제동 없이 차간거리를 잘 지켰어요" else "급제동 ${suddenBrakeCount}회 감지 · 여유 있게 멈춰보세요",
      coachingTip = "앞차 브레이크등이 켜지면 미리 발을 떼고 3초 여유를 두면 부드럽게 멈출 수 있어요."
    ),
    DrivingHabitMetric(
      category = HabitCategory.SMOOTH_ACCELERATION,
      titleKo = "부드럽게 출발하기",
      titleEn = "Smooth Acceleration",
      score = smoothAccelerationScore.coerceIn(0, 100),
      targetScore = 80,
      violationCount = rapidAccelCount,
      weeklyDelta = +3,
      statusComment = if (rapidAccelCount == 0) "급출발 없이 부드럽게 달렸어요" else "급출발 ${rapidAccelCount}회 감지 · 천천히 페달을 밟아주세요",
      coachingTip = "신호 대기 후 출발할 때 처음 3초간 천천히 밟으면 기름값도 12% 아낄 수 있어요."
    ),
    DrivingHabitMetric(
      category = HabitCategory.INDICATOR_USAGE,
      titleKo = "깜빡이 미리 켜기",
      titleEn = "Indicator Usage",
      score = indicatorUsageScore.coerceIn(0, 100),
      targetScore = 80,
      violationCount = missedIndicatorCount,
      weeklyDelta = +5,
      statusComment = if (missedIndicatorCount == 0) "차선 바꿀 때 깜빡이를 잘 켰어요" else "깜빡이 미점등 ${missedIndicatorCount}회 · 차선 바꾸기 3초 전에 켜주세요",
      coachingTip = "차선을 바꾸기 30m 전에 깜빡이를 켜면 뒤차와 안전거리를 지키기 쉬워져요."
    )
  )
}

data class DailyHabitRecord(
  val dayOfWeek: String,
  val dateLabel: String,
  val smoothBrakingScore: Int,
  val smoothAccelerationScore: Int,
  val indicatorUsageScore: Int,
  val distanceKm: Float,
  val suddenBrakeCount: Int = 0,
  val rapidAccelCount: Int = 0,
  val missedIndicatorCount: Int = 0,
  val isToday: Boolean = false
) {
  val summaryScore: Int
    get() = ((smoothBrakingScore + smoothAccelerationScore + indicatorUsageScore) / 3.0).toInt().coerceIn(0, 100)
}

enum class WeeklyReportTrendTab(val labelKo: String, val labelEn: String) {
  OVERALL("종합 점수", "Summary"),
  SMOOTH_BRAKING("부드러운 제동", "Braking"),
  SMOOTH_ACCELERATION("부드러운 가속", "Accel"),
  INDICATOR_USAGE("깜빡이 사용", "Indicator"),
  ALL_COMPARISON("3대 지표 비교", "Compare All")
}

data class WeeklyDrivingReportSummary(
  val records: List<DailyHabitRecord>
) {
  val overallSummaryScore: Int
    get() = if (records.isEmpty()) 0 else records.map { it.summaryScore }.average().toInt()

  val avgSmoothBraking: Int
    get() = if (records.isEmpty()) 0 else records.map { it.smoothBrakingScore }.average().toInt()

  val avgSmoothAcceleration: Int
    get() = if (records.isEmpty()) 0 else records.map { it.smoothAccelerationScore }.average().toInt()

  val avgIndicatorUsage: Int
    get() = if (records.isEmpty()) 0 else records.map { it.indicatorUsageScore }.average().toInt()

  val overallTrendDelta: Int
    get() = calculateTrendDelta { it.summaryScore }

  val brakingTrendDelta: Int
    get() = calculateTrendDelta { it.smoothBrakingScore }

  val accelerationTrendDelta: Int
    get() = calculateTrendDelta { it.smoothAccelerationScore }

  val indicatorTrendDelta: Int
    get() = calculateTrendDelta { it.indicatorUsageScore }

  val totalDistanceKm: Float
    get() = ((records.sumOf { it.distanceKm.toDouble() } * 10.0).toInt() / 10f)

  val totalSuddenBrakes: Int
    get() = records.sumOf { it.suddenBrakeCount }

  val totalRapidAccels: Int
    get() = records.sumOf { it.rapidAccelCount }

  val totalMissedIndicators: Int
    get() = records.sumOf { it.missedIndicatorCount }

  val qualifiedDaysCount: Int
    get() = records.count { it.summaryScore >= 80 }

  val qualifiesForInsuranceDiscount: Boolean
    get() = overallSummaryScore >= 80

  val driverTierTitle: String
    get() = when {
      overallSummaryScore >= 90 -> "상위 3% 퍼펙트 드라이버"
      overallSummaryScore >= 80 -> "보험료 16% 할인 통과"
      overallSummaryScore >= 70 -> "할인까지 ${80 - overallSummaryScore}점 남았어요"
      else -> "조금만 더 부드럽게 운전해 볼까요?"
    }

  val estimatedAnnualSavingsWon: Int
    get() = when {
      overallSummaryScore >= 90 -> 196800
      overallSummaryScore >= 80 -> 172000
      overallSummaryScore >= 71 -> 135000
      else -> 0
    }

  val bestDayRecord: DailyHabitRecord?
    get() = records.maxByOrNull { it.summaryScore }

  val lowestDayRecord: DailyHabitRecord?
    get() = records.minByOrNull { it.summaryScore }

  private fun calculateTrendDelta(selector: (DailyHabitRecord) -> Int): Int {
    if (records.size < 2) return 0
    val firstHalf = records.take(records.size / 2).map(selector).average()
    val secondHalf = records.drop(records.size / 2).map(selector).average()
    return (secondHalf - firstHalf).toInt()
  }
}

data class DriveSession(
  val id: String,
  val dateLabel: String,
  val timeLabel: String,
  val totalKm: Float,
  val durationMinutes: Int,
  val score: Int,
  val rapidAccelCount: Int,
  val suddenBrakeCount: Int,
  val speedingCount: Int,
  val blinkerCompliancePercent: Int,
  val fuelSavedWon: Int,
  val topHighlight: String,
  val topImprovement: String
)

enum class CameraType(val label: String, val shortLabel: String) {
  PARKING_ENFORCEMENT("고정식 주정차", "5분 단속"),
  CHILD_ZONE_PARKING("어린이보호구역", "12만원"),
  IMMEDIATE_TOW("즉시견인구역", "견인 주의"),
  FIRE_HYDRANT("소화전·1분단속", "1분 즉시"),
  LUNCH_GRACE("점심시간 유예", "점심 허용"),
  SPEED_SIGNAL("과속·신호위반", "과속·신호"),
  SECTION_CONTROL("구간단속", "구간단속"),
  BUS_ONLY_LANE("버스전용차로", "버스전용")
}

data class VehicleGpsCoordinate(
  val latitude: Double = 37.5006,
  val longitude: Double = 127.0364,
  val addressLabel: String = "서울 강남구 역삼동 테헤란로 152 부근"
)

data class EnforcementCamera(
  val id: String,
  val title: String,
  val type: CameraType,
  val speedLimit: Int,
  val distanceMeters: Int,
  val roadName: String,
  val activeNotice: String,
  val fineNotice: String,
  val latitude: Double = 37.5015,
  val longitude: Double = 127.0375
) {
  val coordinateText: String
    get() = String.format("%.4f°N, %.4f°E", latitude, longitude)
}

data class VehicleConsumable(
  val id: String,
  val name: String,
  val currentKm: Int,
  val targetIntervalKm: Int,
  val lastReplacedKm: Int,
  val healthPercent: Int,
  val dDayDays: Int,
  val isUrgent: Boolean,
  val maintenanceTip: String
)

data class InsuranceCompanyQuote(
  val id: String,
  val companyName: String,
  val badgeText: String,
  val minScoreRequired: Int,
  val maxDiscountRate: Float,
  val estimatedAnnualSaving: Int,
  val specialContractName: String,
  val benefitsSummary: String
)

data class ChatMessage(
  val id: String,
  val text: String,
  val isUser: Boolean,
  val timestamp: Long = System.currentTimeMillis(),
  val spokenByTts: Boolean = false
)

enum class LineType(val label: String, val canChangeLane: Boolean) {
  DASHED("점선 (차선변경 가능)", true),
  SOLID("실선 (차선변경 금지)", false),
  DOUBLE_SOLID("복선 실선 (절대 금지)", false)
}

enum class CameraMode(val label: String) {
  FRONT("전방 카메라 AI 비전"),
  REAR("후방 카메라 AI 비전")
}

data class LaneDetectionState(
  val leftLineType: LineType = LineType.DASHED,
  val rightLineType: LineType = LineType.SOLID,
  val laneDepartureWarning: Boolean = false,
  val departureSide: String = "",
  val confidence: Float = 0.94f,
  val currentCameraMode: CameraMode = CameraMode.FRONT,
  val isDetecting: Boolean = true
)

enum class TurnSignal {
  OFF, LEFT, RIGHT
}

/**
 * 30-Second Quick Debrief generated by Gleo AI immediately after parking/stopping
 */
data class QuickDebriefData(
  val title: String,
  val praiseComment: String,
  val onePointTip: String,
  val speechText: String,
  val fuelSavedWon: Int,
  val earnedPoints: Int,
  val finalScore: Int,
  val distanceKm: Float,
  val durationMinutes: Int
)

/**
 * 주정차 단속 유예 '골든타임' 자동 카운트다운 모델
 */
data class ParkingGracePeriodState(
  val isTracking: Boolean = false,
  val cameraName: String = "선릉역 교차로 주정차 단속",
  val graceTotalSeconds: Int = 600, // 기본 10분 유예
  val remainingSeconds: Int = 600,
  val isLunchTimeGrace: Boolean = false, // 11:30~14:00 점심시간 유예
  val hasWarned5Min: Boolean = false,
  val isExpired: Boolean = false
) {
  val remainingFormatted: String
    get() {
      val m = (remainingSeconds.coerceAtLeast(0)) / 60
      val s = (remainingSeconds.coerceAtLeast(0)) % 60
      return String.format("%02d:%02d", m, s)
    }

  val progressPercent: Float
    get() = if (graceTotalSeconds <= 0) 0f else (remainingSeconds.toFloat() / graceTotalSeconds).coerceIn(0f, 1f)
}

/**
 * 리텐션 확대를 위한 데일리 마이크로 리워드 & 출퇴근 안전 챌린지 모델
 */
data class CommuteChallenge(
  val id: String,
  val title: String,
  val subtitle: String,
  val rewardTitle: String,
  val rewardTag: String,
  val iconEmoji: String,
  val currentProgress: Int,
  val targetProgress: Int,
  val periodLabel: String,
  val isCompleted: Boolean = false,
  val isClaimed: Boolean = false
) {
  val progressPercent: Float
    get() = if (targetProgress <= 0) 0f else (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f)
}

/**
 * 챌린지 달성 및 포인트로 발급받은 모바일 쿠폰함 모델
 */
data class IssuedCoupon(
  val id: String,
  val title: String,
  val brand: String,
  val barcodeNumber: String,
  val expiryDate: String,
  val valueLabel: String,
  val iconEmoji: String,
  val isUsed: Boolean = false
)

/**
 * 카메라 비전 활용 주차장 위치 기억 & 문콕 방어 가이드 모델
 */
data class ParkingMemoryData(
  val floor: String = "지하 3층 (B3)",
  val pillarNumber: String = "D-12 기둥 앞",
  val parkingTime: String = "오늘 14:30 주차",
  val leftClearanceCm: Int = 68,
  val rightClearanceCm: Int = 42,
  val defenseAdvice: String = "기둥에 10cm 바짝 붙여 주차해서, 운전석 문콕 걱정 없어요!",
  val isScanned: Boolean = true
)

/**
 * 복잡한 주차 요금 표지판 '1초 번역기' 모델
 */
data class ParkingFeeData(
  val parkingLotName: String = "강남 파이낸스센터(GFC) 주차장",
  val baseRateText: String = "기본 30분 3,000원",
  val extraRateText: String = "추가 10분당 1,000원",
  val maxDailyRateWon: Int = 30000,
  val freeGraceMinutes: Int = 15,
  val selectedHours: Int = 2
) {
  val calculatedFeeWon: Int
    get() {
      if (selectedHours <= 0) return 0
      val totalMinutes = selectedHours * 60
      if (totalMinutes <= freeGraceMinutes) return 0
      val billableMinutes = totalMinutes - 30
      val extraUnits = (billableMinutes.coerceAtLeast(0) + 9) / 10
      val fee = 3000 + (extraUnits * 1000)
      return fee.coerceAtMost(maxDailyRateWon)
    }

  val summaryBriefing: String
    get() = "예상 주차 ${selectedHours}시간 기준 최종 요금은 ${String.format("%,d", calculatedFeeWon)}원이에요. 15분 안에 나가면 주차비는 무료예요."
}
