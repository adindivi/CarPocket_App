package com.example.data

import android.content.Context
import com.example.model.ChatMessage
import com.example.model.DailyHabitRecord
import com.example.model.DriveSession
import com.example.model.DrivingHabitTrackerData
import com.example.model.VehicleConsumable
import org.json.JSONArray
import org.json.JSONObject

class DrivingHabitHistoryRepository(context: Context) {

  companion object {
    private const val PREFS_NAME = "safe_coach_habit_history_prefs"
    private const val KEY_WEEKLY_RECORDS = "weekly_habit_records_v1"
    private const val KEY_RECENT_TRIPS = "recent_trips_v1"
    private const val KEY_ACCUMULATED_POINTS = "accumulated_points_v1"
    private const val KEY_CONSUMABLES = "vehicle_consumables_v1"
    private const val KEY_CHAT_MESSAGES = "chat_messages_v1"
  }

  private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun loadWeeklyRecords(): List<DailyHabitRecord> {
    val rawJson = prefs.getString(KEY_WEEKLY_RECORDS, null)
    if (rawJson.isNullOrBlank()) {
      val defaults = defaultSevenDayRecords()
      saveWeeklyRecords(defaults)
      return defaults
    }

    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<DailyHabitRecord>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          DailyHabitRecord(
            dayOfWeek = obj.optString("dayOfWeek", "일"),
            dateLabel = obj.optString("dateLabel", ""),
            smoothBrakingScore = obj.optInt("smoothBrakingScore", 90),
            smoothAccelerationScore = obj.optInt("smoothAccelerationScore", 90),
            indicatorUsageScore = obj.optInt("indicatorUsageScore", 90),
            distanceKm = obj.optDouble("distanceKm", 18.0).toFloat(),
            suddenBrakeCount = obj.optInt("suddenBrakeCount", 0),
            rapidAccelCount = obj.optInt("rapidAccelCount", 0),
            missedIndicatorCount = obj.optInt("missedIndicatorCount", 0),
            isToday = obj.optBoolean("isToday", i == array.length() - 1)
          )
        )
      }
      if (list.size == 7) list else defaultSevenDayRecords()
    } catch (e: Exception) {
      defaultSevenDayRecords()
    }
  }

  fun saveWeeklyRecords(records: List<DailyHabitRecord>) {
    try {
      val array = JSONArray()
      records.forEach { rec ->
        val obj = JSONObject().apply {
          put("dayOfWeek", rec.dayOfWeek)
          put("dateLabel", rec.dateLabel)
          put("smoothBrakingScore", rec.smoothBrakingScore)
          put("smoothAccelerationScore", rec.smoothAccelerationScore)
          put("indicatorUsageScore", rec.indicatorUsageScore)
          put("distanceKm", rec.distanceKm.toDouble())
          put("suddenBrakeCount", rec.suddenBrakeCount)
          put("rapidAccelCount", rec.rapidAccelCount)
          put("missedIndicatorCount", rec.missedIndicatorCount)
          put("isToday", rec.isToday)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_WEEKLY_RECORDS, array.toString()).apply()
    } catch (_: Exception) {
    }
  }

  fun updateTodayRecordWithHabitTracker(
    currentRecords: List<DailyHabitRecord>,
    habitTracker: DrivingHabitTrackerData,
    additionalDistanceKm: Float = 0f
  ): List<DailyHabitRecord> {
    val baseList = if (currentRecords.size == 7) currentRecords else defaultSevenDayRecords()
    val updated = baseList.mapIndexed { index, record ->
      if (index == baseList.lastIndex || record.isToday) {
        record.copy(
          smoothBrakingScore = habitTracker.smoothBrakingScore.coerceIn(0, 100),
          smoothAccelerationScore = habitTracker.smoothAccelerationScore.coerceIn(0, 100),
          indicatorUsageScore = habitTracker.indicatorUsageScore.coerceIn(0, 100),
          suddenBrakeCount = habitTracker.suddenBrakeCount,
          rapidAccelCount = habitTracker.rapidAccelCount,
          missedIndicatorCount = habitTracker.missedIndicatorCount,
          distanceKm = ((record.distanceKm + additionalDistanceKm) * 10f).toInt() / 10f,
          isToday = true
        )
      } else {
        record
      }
    }
    saveWeeklyRecords(updated)
    return updated
  }

  fun resetToDefaultRecords(): List<DailyHabitRecord> {
    val defaults = defaultSevenDayRecords()
    saveWeeklyRecords(defaults)
    return defaults
  }

  fun defaultSevenDayRecords(): List<DailyHabitRecord> = listOf(
    DailyHabitRecord(
      dayOfWeek = "월",
      dateLabel = "10/23 (월)",
      smoothBrakingScore = 84,
      smoothAccelerationScore = 88,
      indicatorUsageScore = 86,
      distanceKm = 24.5f,
      suddenBrakeCount = 2,
      rapidAccelCount = 1,
      missedIndicatorCount = 1
    ),
    DailyHabitRecord(
      dayOfWeek = "화",
      dateLabel = "10/24 (화)",
      smoothBrakingScore = 88,
      smoothAccelerationScore = 91,
      indicatorUsageScore = 90,
      distanceKm = 21.2f,
      suddenBrakeCount = 1,
      rapidAccelCount = 1,
      missedIndicatorCount = 0
    ),
    DailyHabitRecord(
      dayOfWeek = "수",
      dateLabel = "10/25 (수)",
      smoothBrakingScore = 92,
      smoothAccelerationScore = 95,
      indicatorUsageScore = 94,
      distanceKm = 19.8f,
      suddenBrakeCount = 0,
      rapidAccelCount = 0,
      missedIndicatorCount = 0
    ),
    DailyHabitRecord(
      dayOfWeek = "목",
      dateLabel = "10/26 (목)",
      smoothBrakingScore = 86,
      smoothAccelerationScore = 90,
      indicatorUsageScore = 89,
      distanceKm = 28.4f,
      suddenBrakeCount = 1,
      rapidAccelCount = 1,
      missedIndicatorCount = 1
    ),
    DailyHabitRecord(
      dayOfWeek = "금",
      dateLabel = "10/27 (금)",
      smoothBrakingScore = 94,
      smoothAccelerationScore = 96,
      indicatorUsageScore = 95,
      distanceKm = 31.0f,
      suddenBrakeCount = 0,
      rapidAccelCount = 0,
      missedIndicatorCount = 0
    ),
    DailyHabitRecord(
      dayOfWeek = "토",
      dateLabel = "10/28 (토)",
      smoothBrakingScore = 95,
      smoothAccelerationScore = 97,
      indicatorUsageScore = 96,
      distanceKm = 64.2f,
      suddenBrakeCount = 0,
      rapidAccelCount = 0,
      missedIndicatorCount = 0
    ),
    DailyHabitRecord(
      dayOfWeek = "일",
      dateLabel = "오늘 (일)",
      smoothBrakingScore = 94,
      smoothAccelerationScore = 98,
      indicatorUsageScore = 95,
      distanceKm = 18.4f,
      suddenBrakeCount = 0,
      rapidAccelCount = 0,
      missedIndicatorCount = 0,
      isToday = true
    )
  )

  // 1. Recent Trips Persistence
  fun loadRecentTrips(): List<DriveSession> {
    val rawJson = prefs.getString(KEY_RECENT_TRIPS, null)
    if (rawJson.isNullOrBlank()) {
      val defaults = defaultRecentTrips()
      saveRecentTrips(defaults)
      return defaults
    }
    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<DriveSession>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          DriveSession(
            id = obj.optString("id", "trip_$i"),
            dateLabel = obj.optString("dateLabel", "최근 주행"),
            timeLabel = obj.optString("timeLabel", "30분 소요"),
            totalKm = obj.optDouble("totalKm", 15.0).toFloat(),
            durationMinutes = obj.optInt("durationMinutes", 30),
            score = obj.optInt("score", 90),
            rapidAccelCount = obj.optInt("rapidAccelCount", 0),
            suddenBrakeCount = obj.optInt("suddenBrakeCount", 0),
            speedingCount = obj.optInt("speedingCount", 0),
            blinkerCompliancePercent = obj.optInt("blinkerCompliancePercent", 100),
            fuelSavedWon = obj.optInt("fuelSavedWon", 1500),
            topHighlight = obj.optString("topHighlight", "안전거리 유지 우수"),
            topImprovement = obj.optString("topImprovement", "신호 대기 출발 여유")
          )
        )
      }
      if (list.isNotEmpty()) list else defaultRecentTrips()
    } catch (_: Exception) {
      defaultRecentTrips()
    }
  }

  fun saveRecentTrips(trips: List<DriveSession>) {
    try {
      val array = JSONArray()
      trips.take(30).forEach { trip ->
        val obj = JSONObject().apply {
          put("id", trip.id)
          put("dateLabel", trip.dateLabel)
          put("timeLabel", trip.timeLabel)
          put("totalKm", trip.totalKm.toDouble())
          put("durationMinutes", trip.durationMinutes)
          put("score", trip.score)
          put("rapidAccelCount", trip.rapidAccelCount)
          put("suddenBrakeCount", trip.suddenBrakeCount)
          put("speedingCount", trip.speedingCount)
          put("blinkerCompliancePercent", trip.blinkerCompliancePercent)
          put("fuelSavedWon", trip.fuelSavedWon)
          put("topHighlight", trip.topHighlight)
          put("topImprovement", trip.topImprovement)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_RECENT_TRIPS, array.toString()).apply()
    } catch (_: Exception) {
    }
  }

  private fun defaultRecentTrips(): List<DriveSession> = listOf(
    DriveSession(
      id = "trip_1",
      dateLabel = "오늘 08:35 출근길",
      timeLabel = "38분 소요",
      totalKm = 18.4f,
      durationMinutes = 38,
      score = 94,
      rapidAccelCount = 0,
      suddenBrakeCount = 0,
      speedingCount = 0,
      blinkerCompliancePercent = 100,
      fuelSavedWon = 1850,
      topHighlight = "부드러운 정속 주행으로 연비 12% 향상",
      topImprovement = "올림픽대로 합류 구간 안전거리 유지 탁월"
    ),
    DriveSession(
      id = "trip_2",
      dateLabel = "어제 19:10 퇴근길",
      timeLabel = "45분 소요",
      totalKm = 19.1f,
      durationMinutes = 45,
      score = 88,
      rapidAccelCount = 1,
      suddenBrakeCount = 1,
      speedingCount = 0,
      blinkerCompliancePercent = 90,
      fuelSavedWon = 1200,
      topHighlight = "야간 주행 중 규정 속도 100% 준수",
      topImprovement = "퇴근 정체 구간 급출발 1회 주의"
    )
  )

  // 2. Accumulated Points Persistence
  fun loadAccumulatedPoints(): Int {
    return prefs.getInt(KEY_ACCUMULATED_POINTS, 14250)
  }

  fun saveAccumulatedPoints(points: Int) {
    prefs.edit().putInt(KEY_ACCUMULATED_POINTS, points).apply()
  }

  // 3. Vehicle Consumables Persistence
  fun loadConsumables(): List<VehicleConsumable> {
    val rawJson = prefs.getString(KEY_CONSUMABLES, null)
    if (rawJson.isNullOrBlank()) {
      val defaults = defaultConsumables()
      saveConsumables(defaults)
      return defaults
    }
    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<VehicleConsumable>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          VehicleConsumable(
            id = obj.optString("id", "item_$i"),
            name = obj.optString("name", ""),
            currentKm = obj.optInt("currentKm", 0),
            targetIntervalKm = obj.optInt("targetIntervalKm", 10000),
            lastReplacedKm = obj.optInt("lastReplacedKm", 0),
            healthPercent = obj.optInt("healthPercent", 100),
            dDayDays = obj.optInt("dDayDays", 30),
            isUrgent = obj.optBoolean("isUrgent", false),
            maintenanceTip = obj.optString("maintenanceTip", "")
          )
        )
      }
      if (list.isNotEmpty()) list else defaultConsumables()
    } catch (_: Exception) {
      defaultConsumables()
    }
  }

  fun saveConsumables(items: List<VehicleConsumable>) {
    try {
      val array = JSONArray()
      items.forEach { item ->
        val obj = JSONObject().apply {
          put("id", item.id)
          put("name", item.name)
          put("currentKm", item.currentKm)
          put("targetIntervalKm", item.targetIntervalKm)
          put("lastReplacedKm", item.lastReplacedKm)
          put("healthPercent", item.healthPercent)
          put("dDayDays", item.dDayDays)
          put("isUrgent", item.isUrgent)
          put("maintenanceTip", item.maintenanceTip)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_CONSUMABLES, array.toString()).apply()
    } catch (_: Exception) {
    }
  }

  private fun defaultConsumables(): List<VehicleConsumable> = listOf(
    VehicleConsumable(
      id = "item_engine_oil",
      name = "엔진오일 & 필터",
      currentKm = 8400,
      targetIntervalKm = 10000,
      lastReplacedKm = 34000,
      healthPercent = 16,
      dDayDays = 14,
      isUrgent = false,
      maintenanceTip = "앞으로 1,600km 더 타고 교체하세요. 순정 0W-30 오일을 추천해요."
    ),
    VehicleConsumable(
      id = "item_brake_pad",
      name = "브레이크 패드",
      currentKm = 31200,
      targetIntervalKm = 40000,
      lastReplacedKm = 11000,
      healthPercent = 22,
      dDayDays = 68,
      isUrgent = false,
      maintenanceTip = "두께가 넉넉해요. 평소처럼 부드럽게 멈추면 30% 더 오래 쓸 수 있어요."
    ),
    VehicleConsumable(
      id = "item_aircon_filter",
      name = "에어컨 / 히터 필터",
      currentKm = 11200,
      targetIntervalKm = 10000,
      lastReplacedKm = 31100,
      healthPercent = 0,
      dDayDays = -5,
      isUrgent = true,
      maintenanceTip = "교체 시기가 지났어요. 상쾌한 공기를 위해 새 필터로 바꿔주세요."
    ),
    VehicleConsumable(
      id = "item_tire",
      name = "타이어 마모도",
      currentKm = 38200,
      targetIntervalKm = 45000,
      lastReplacedKm = 4100,
      healthPercent = 15,
      dDayDays = 35,
      isUrgent = false,
      maintenanceTip = "홈 깊이가 3.1mm 남았어요. 빗길 미끄럼 방지를 위해 곧 점검해보세요."
    ),
    VehicleConsumable(
      id = "item_battery",
      name = "차량 배터리",
      currentKm = 42350,
      targetIntervalKm = 60000,
      lastReplacedKm = 0,
      healthPercent = 88,
      dDayDays = 340,
      isUrgent = false,
      maintenanceTip = "배터리 전압이 14.2V로 아주 건강해요."
    )
  )

  // 4. Chat Messages Persistence
  fun loadChatMessages(): List<ChatMessage> {
    val rawJson = prefs.getString(KEY_CHAT_MESSAGES, null)
    if (rawJson.isNullOrBlank()) {
      val defaults = defaultChatMessages()
      saveChatMessages(defaults)
      return defaults
    }
    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<ChatMessage>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          ChatMessage(
            id = obj.optString("id", "m_$i"),
            text = obj.optString("text", ""),
            isUser = obj.optBoolean("isUser", false),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
            spokenByTts = obj.optBoolean("spokenByTts", false)
          )
        )
      }
      if (list.isNotEmpty()) list else defaultChatMessages()
    } catch (_: Exception) {
      defaultChatMessages()
    }
  }

  fun saveChatMessages(messages: List<ChatMessage>) {
    try {
      val array = JSONArray()
      messages.takeLast(50).forEach { msg ->
        val obj = JSONObject().apply {
          put("id", msg.id)
          put("text", msg.text)
          put("isUser", msg.isUser)
          put("timestamp", msg.timestamp)
          put("spokenByTts", msg.spokenByTts)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_CHAT_MESSAGES, array.toString()).apply()
    } catch (_: Exception) {
    }
  }

  private fun defaultChatMessages(): List<ChatMessage> = listOf(
    ChatMessage(
      id = "m_1",
      text = "반가워요! 안전운전 짝꿍 글레오예요. 오늘 주행 점수나 기름값 절약액, 보험료 할인까지 무엇이든 편하게 물어보세요.",
      isUser = false,
      spokenByTts = true
    )
  )
}
