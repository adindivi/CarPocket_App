package com.example.domain

/**
 * Domain Telematics Constants
 * Replaces magic numbers with meaningful domain constants (Clean Code / DRY).
 */
object TelematicsConstants {
  // Score thresholds
  const val SAFE_SCORE_THRESHOLD: Int = 80
  const val WARNING_SCORE_THRESHOLD: Int = 70
  const val INITIAL_PERFECT_SCORE: Int = 100

  // Speed limits & defaults (km/h)
  const val DEFAULT_SPEED_LIMIT_KMH: Int = 60
  const val INITIAL_CRUISING_SPEED_KMH: Int = 35
  const val MIN_CRUISING_SPEED_KMH: Int = 30
  const val MAX_CRUISING_SPEED_KMH: Int = 68
  const val ZERO_SPEED_THRESHOLD_KMH: Float = 3.0f

  // Reward points calculation
  const val POINTS_PER_KM: Int = 50
  const val POINTS_PER_SCORE_MULTIPLIER: Int = 2
  const val INITIAL_POINTS_BALANCE: Int = 14250

  // Fuel economy calculation (KRW saved per km of smooth driving)
  const val FUEL_SAVED_WON_PER_KM: Int = 110

  // Aggressive driving cost penalty (Real currency conversion)
  const val RAPID_ACCEL_COST_WON: Int = 180 // Average excess fuel burned per rapid start
  const val SUDDEN_BRAKE_COST_WON: Int = 120 // Brake pad wear & kinetic energy lost per sudden brake
  const val COFFEE_PRICE_WON: Int = 3800 // Daily life comparison benchmark

  // Road rage & sudden reaction detection window
  const val ROAD_RAGE_WINDOW_MS: Long = 4000L // 4 seconds between hard brake and sudden acceleration

  // CCTV Warning range (meters)
  const val CCTV_ALERT_MIN_DISTANCE_METERS: Int = 180
  const val CCTV_ALERT_MAX_DISTANCE_METERS: Int = 210
  const val CCTV_RESET_DISTANCE_METERS: Int = 30

  // Parking grace period timings (seconds)
  const val PARKING_GRACE_DEFAULT_SECONDS: Int = 600
  const val PARKING_GRACE_5MIN_WARNING_SECONDS: Int = 300

  // UI & Alert auto-dismiss timings (milliseconds)
  const val ROAD_RAGE_CALM_DISPLAY_DURATION_MS: Long = 4500L
  const val COST_FLOATING_MESSAGE_DURATION_MS: Long = 2200L
  const val BANNER_MESSAGE_DURATION_MS: Long = 5000L
  const val LANE_DEPARTURE_WARNING_DURATION_MS: Long = 4000L

  // Driving habit penalties & floors
  const val HABIT_PENALTY_BRAKING: Int = 8
  const val HABIT_PENALTY_ACCEL: Int = 8
  const val HABIT_PENALTY_INDICATOR: Int = 7
  const val HABIT_MIN_FLOOR_SCORE: Int = 40

  // Vehicle Defaults
  const val DEFAULT_ODOMETER_KM: Int = 42350
  const val DEFAULT_VEHICLE_MODEL: String = "제네시스 GV70 (12가 3456)"

  /**
   * Calculates earned safe points from trip distance and final safety score.
   */
  fun calculateEarnedPoints(distanceKm: Float, finalScore: Int): Int {
    return (distanceKm * POINTS_PER_KM).toInt() + (finalScore * POINTS_PER_SCORE_MULTIPLIER)
  }

  /**
   * Calculates estimated fuel savings in KRW from distance.
   */
  fun calculateFuelSavingsWon(distanceKm: Float): Int {
    return (distanceKm * FUEL_SAVED_WON_PER_KM).toInt()
  }

  /**
   * Calculates immediate economic loss from aggressive acceleration and braking.
   */
  fun calculateAggressiveCostWon(rapidAccelCount: Int, suddenBrakeCount: Int): Int {
    return (rapidAccelCount * RAPID_ACCEL_COST_WON) + (suddenBrakeCount * SUDDEN_BRAKE_COST_WON)
  }

  /**
   * Translates saved fuel currency into friendly everyday benchmarks (Toss style).
   */
  fun formatFuelSavingComparison(fuelSavedWon: Int): String {
    return when {
      fuelSavedWon >= 10000 -> "점심 한 끼 값(${fuelSavedWon}원)을 아꼈어요"
      fuelSavedWon >= COFFEE_PRICE_WON -> "시원한 커피 한 잔 값(${fuelSavedWon}원)을 아꼈어요"
      fuelSavedWon >= 1500 -> "달콤한 음료수 한 캔 값(${fuelSavedWon}원)을 아꼈어요"
      fuelSavedWon > 0 -> "부드럽게 달려 ${fuelSavedWon}원을 아끼는 중이에요"
      else -> "부드럽게 출발하고 멈추면 기름값을 아껴요"
    }
  }
}
