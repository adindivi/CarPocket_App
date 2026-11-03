package com.example.domain

import com.example.model.DrivingEvent
import com.example.model.DrivingEventType
import com.example.model.EventSeverity
import java.util.UUID

/**
 * Factory pattern creating standardized DrivingEvent instances (Clean Code / SRP).
 * Encapsulates titles, explanations, default speeches, and severity mappings.
 */
object DrivingEventFactory {

  fun create(
    type: DrivingEventType,
    currentSpeedKmh: Int,
    voiceSpeechOverride: String? = null
  ): DrivingEvent {
    val speed = currentSpeedKmh.coerceAtLeast(35)
    return when (type) {
      DrivingEventType.RAPID_ACCEL -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "급출발 감지 (+0.45G)",
        message = "천천히 밟으면 기름값과 부품을 아낄 수 있어요.",
        voiceSpeech = voiceSpeechOverride ?: "급출발이 감지되었어요. 부드럽게 출발해 주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.WARNING
      )
      DrivingEventType.SUDDEN_BRAKE -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "급제동 감지 (-0.58G)",
        message = "뒤차와의 충돌 위험이 있어요. 3초 안전거리를 유지해 주세요.",
        voiceSpeech = voiceSpeechOverride ?: "급제동이 감지되었어요. 충분한 안전거리를 유지해 주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.CRITICAL
      )
      DrivingEventType.SHARP_TURN -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "급회전 감지",
        message = "코너 돌기 전에 미리 속도를 줄이면 안전해요.",
        voiceSpeech = voiceSpeechOverride ?: "급회전이 감지되었어요. 커브 돌기 전에 속도를 줄여주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.WARNING
      )
      DrivingEventType.SPEEDING -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "제한속도 초과 (+22km/h)",
        message = "제한속도 60km/h 구간이에요. 속도를 조금 줄여주세요.",
        voiceSpeech = voiceSpeechOverride ?: "제한속도를 초과했어요. 속도를 줄여주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = 82,
        severity = EventSeverity.CRITICAL
      )
      DrivingEventType.UNBUCKLED_SEATBELT -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "안전벨트를 매주세요",
        message = "나와 가족의 안전을 위해 출발 전 벨트를 꼭 매주세요.",
        voiceSpeech = voiceSpeechOverride ?: "안전벨트를 매주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.CRITICAL
      )
      DrivingEventType.NO_BLINKER_LANE_CHANGE -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "깜빡이를 켜주세요",
        message = "차선을 바꾸기 30m 전에 깜빡이를 켜면 뒤차가 안전해요.",
        voiceSpeech = voiceSpeechOverride ?: "차선 바꾸기 전에 깜빡이를 미리 켜주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.WARNING
      )
      DrivingEventType.SOLID_LANE_CROSS -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "실선 구간 차선변경",
        message = "터널과 다리 위 실선 구간은 차선을 바꿀 수 없어요.",
        voiceSpeech = voiceSpeechOverride ?: "실선 구간이에요. 차선을 바꿀 수 없어요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.CRITICAL
      )
      DrivingEventType.SAFE_DISTANCE_WARNING -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "앞차와 거리가 가까워요",
        message = "앞차와의 거리가 좁아요. 3초 여유를 두세요.",
        voiceSpeech = voiceSpeechOverride ?: "앞차와 너무 가까워요. 안전거리를 유지해 주세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.WARNING
      )
      DrivingEventType.ROAD_RAGE_ALERT -> DrivingEvent(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "3초 마음 진정 케어",
        message = "심호흡 한 번 깊게 쉬고, 안전거리를 유지해 보세요.",
        voiceSpeech = voiceSpeechOverride ?: "방금 상황에 많이 놀라셨죠? 심호흡 한 번 깊게 쉬고 거리를 띄워보세요.",
        timestamp = System.currentTimeMillis(),
        speedKmh = speed,
        severity = EventSeverity.INFO
      )
    }
  }
}
