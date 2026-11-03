package com.example.ai

import com.example.domain.TelematicsConstants
import com.example.model.DriveSession
import com.example.model.DrivingHabitTrackerData
import com.example.model.EnforcementCamera
import com.example.model.QuickDebriefData
import com.example.util.Formatters

/**
 * Gleo AI Conversational Engine
 * Handles natural language intent classification, 30s quick debriefing, and emotion care (SRP).
 */
class GleoAiEngine {

  fun generateAnswer(
    query: String,
    currentScore: Int,
    totalOdometerKm: Int,
    nearestCctv: EnforcementCamera?
  ): String {
    val q = query.lowercase().trim()

    return when {
      matchesScoreIntent(q) -> {
        "이번 주 종합 운전점수는 ${currentScore}점으로 아주 훌륭해요! 제한속도도 잘 지키셨고 급제동도 많이 줄었어요. 지금처럼 안전하게 운전하시면 자동차 보험료를 최대 16%까지 아낄 수 있어요."
      }
      matchesBrakeIntent(q) -> {
        "오늘 급제동은 딱 1번 있었어요. 테헤란로 사거리에서 신호가 바뀔 때 살짝 급하게 멈추셨네요. 앞차와 거리를 3초만 더 두면 훨씬 부드럽게 멈출 수 있어요."
      }
      matchesInsuranceIntent(q) -> {
        "현재 ${currentScore}점 기준으로 매년 약 196,800원의 자동차 보험료를 바로 아끼실 수 있어요! '보험할인' 탭에서 원하는 보험사로 간편하게 신청해 보세요."
      }
      matchesMaintenanceIntent(q) -> {
        "지금까지 총 ${totalOdometerKm}km 달렸어요. 엔진오일은 앞으로 1,600km(약 2주) 더 타고 교체하시면 딱 좋아요. 에어컨 필터는 교체 시기가 지났으니 상쾌한 공기를 위해 곧 바꿔주세요."
      }
      matchesCameraIntent(q) -> {
        if (nearestCctv != null) {
          "${nearestCctv.distanceMeters}m 앞에 시속 ${nearestCctv.speedLimit}km ${nearestCctv.type.shortLabel} 단속 카메라가 있어요. 안전속도를 지켜주세요!"
        } else {
          "지금 지나가는 경로 주변엔 단속 카메라가 없어요. 편안하고 안전하게 운전하세요!"
        }
      }
      matchesLaneIntent(q) -> {
        "차선 바꿀 때는 3초 전에 깜빡이를 미리 켜주세요. 실선 구간은 차선 변경 금지구역이니 꼭 점선에서 안전하게 이동해 주세요."
      }
      matchesCarLocationIntent(q) -> {
        "내 차는 지하 3층 D-12 기둥 앞에 안전하게 주차되어 있어요. 옆 차와 간격도 넉넉해서 문콕 걱정 없으니 안심하세요!"
      }
      matchesParkingFeeIntent(q) -> {
        "현재 주차장은 2시간 주차 시 1만 2,000원이 나올 예정이에요. 15분 안에 나가면 주차비는 무료예요!"
      }
      else -> {
        "무엇이든 편하게 물어보세요! 오늘 운전 점수나 단속 카메라, 소모품 교체 시기, 보험료 할인까지 알기 쉽게 알려드릴게요."
      }
    }
  }

  private fun matchesScoreIntent(q: String): Boolean =
    q.contains("운전") && (q.contains("어땠") || q.contains("평가") || q.contains("점수"))

  private fun matchesBrakeIntent(q: String): Boolean =
    q.contains("급제동") || q.contains("브레이크") || q.contains("급정거")

  private fun matchesInsuranceIntent(q: String): Boolean =
    q.contains("보험") || q.contains("할인") || q.contains("얼마")

  private fun matchesMaintenanceIntent(q: String): Boolean =
    q.contains("엔진오일") || q.contains("소모품") || q.contains("정비") || q.contains("필터")

  private fun matchesCameraIntent(q: String): Boolean =
    q.contains("단속") || q.contains("카메라") || q.contains("cctv")

  private fun matchesLaneIntent(q: String): Boolean =
    q.contains("차선") || q.contains("깜빡이") || q.contains("신호")

  private fun matchesCarLocationIntent(q: String): Boolean =
    (q.contains("차") && (q.contains("어디") || q.contains("위치") || q.contains("기둥") || q.contains("주차장"))) || q.contains("문콕")

  private fun matchesParkingFeeIntent(q: String): Boolean =
    q.contains("주차비") || q.contains("주차 요금") || q.contains("번역") || q.contains("요금표")

  /**
   * Generates a 30-second post-drive Quick Debrief pairing the driver's top praise with one actionable tip.
   */
  fun generateQuickDebrief(
    session: DriveSession,
    habitData: DrivingHabitTrackerData,
    fuelSavedWon: Int,
    earnedPoints: Int
  ): QuickDebriefData {
    val brakingScore = habitData.smoothBrakingScore
    val accelScore = habitData.smoothAccelerationScore
    val indicatorScore = habitData.indicatorUsageScore

    // Determine top praise based on highest metric or achievement
    val praise = when {
      accelScore >= 95 -> "오늘 부드러운 가속은 ${accelScore}점으로 승차감과 연비 모두 완벽했어요!"
      brakingScore >= 95 -> "앞차와 3초 안전거리를 유지해 급제동 없이 부드럽게 잘 멈췄어요!"
      indicatorScore >= 95 -> "차선 변경 시 깜빡이 준수율 100%로 매너 있는 주행이 돋보였어요!"
      fuelSavedWon >= TelematicsConstants.COFFEE_PRICE_WON -> "부드럽게 정속 주행해서 시원한 커피 한 잔 값(${Formatters.formatWon(fuelSavedWon)})을 아꼈어요!"
      else -> "목적지까지 안전하게 잘 도착했어요. 오늘 운전점수는 ${session.score}점이에요!"
    }

    // Determine one actionable tip based on lowest score
    val tip = when {
      brakingScore < 85 -> "다음 주행 땐 정지선 30m 전부터 브레이크를 살짝 나눠 밟아보세요."
      accelScore < 85 -> "신호 대기 후 출발할 때 처음 3초간 페달을 지그시 밟아주시면 더 좋아요."
      indicatorScore < 85 -> "차선 변경 3초 전에 깜빡이를 미리 켜주시면 뒤차와의 안전거리가 훨씬 넓어져요."
      session.speedingCount > 0 -> "어린이보호구역이나 단속 카메라 진입 전 감속 타이밍만 2초 먼저 챙겨보세요."
      else -> "지금처럼 안전하게 운전하시면 자동차 보험료를 최대 16%까지 아낄 수 있어요."
    }

    val speechText = "오늘 주행도 수고하셨어요! $praise $tip"

    return QuickDebriefData(
      title = "오늘의 주행 요약",
      praiseComment = praise,
      onePointTip = tip,
      speechText = speechText,
      fuelSavedWon = fuelSavedWon,
      earnedPoints = earnedPoints,
      finalScore = session.score,
      distanceKm = session.totalKm,
      durationMinutes = session.durationMinutes
    )
  }

  /**
   * Generates calming emotion-care speech for sudden aggression or road-rage prevention.
   */
  fun generateRoadRageCalmSpeech(): String =
    "방금 상황에 많이 놀라셨죠? 심호흡 한 번 깊게 쉬고, 앞차와 거리를 조금 띄워볼까요?"
}
