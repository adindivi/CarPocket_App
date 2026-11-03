package com.example.telematics

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Telematics Sensor & GPS Noise Filter
 * 
 * Provides:
 * 1. Gravity separation via Low-Pass Filter (LPF)
 * 2. False-positive spike filtering for potholes and speed bumps
 * 3. 500ms duration window verification for true sudden braking / rapid acceleration
 * 4. Zero-Speed Clamping to eliminate stationary GPS drift in urban canyons
 */
class DrivingSensorFilter {

  companion object {
    private const val LPF_ALPHA = 0.85f // Low-pass filter smoothing factor for gravity
    private const val BRAKE_G_THRESHOLD = -0.38f // G-force threshold for sudden braking
    private const val ACCEL_G_THRESHOLD = 0.35f  // G-force threshold for rapid acceleration
    private const val MIN_EVENT_DURATION_MS = 500L // Minimum sustained duration (ms) to reject pothole spikes
    private const val MAX_SPIKE_DURATION_MS = 250L // Shock impulses shorter than this are classified as bumps
    private const val ZERO_SPEED_THRESHOLD_KMH = 3.0f // Below 3km/h, clamp to zero
    private const val MAX_ACCEPTABLE_GPS_ACCURACY_METERS = 25.0f
  }

  // Estimated gravity vector [X, Y, Z]
  private var gravity = floatArrayOf(0f, 0f, 9.8f)

  // Recent linear acceleration tracking for temporal windowing
  private var brakeStartTime: Long = 0L
  private var accelStartTime: Long = 0L
  private var lastHardBrakeTimestampMs: Long = 0L

  /**
   * Filters raw 3-axis accelerometer reading into gravity-compensated linear acceleration.
   * Returns [linearX, linearY, linearZ] in m/s^2.
   */
  fun extractLinearAcceleration(rawX: Float, rawY: Float, rawZ: Float): FloatArray {
    gravity[0] = LPF_ALPHA * gravity[0] + (1 - LPF_ALPHA) * rawX
    gravity[1] = LPF_ALPHA * gravity[1] + (1 - LPF_ALPHA) * rawY
    gravity[2] = LPF_ALPHA * gravity[2] + (1 - LPF_ALPHA) * rawZ

    val linearX = rawX - gravity[0]
    val linearY = rawY - gravity[1]
    val linearZ = rawZ - gravity[2]

    return floatArrayOf(linearX, linearY, linearZ)
  }

  /**
   * Converts linear acceleration (m/s^2) to G-Force along the vehicle forward axis.
   */
  fun calculateForwardGForce(linearForwardMs2: Float): Float {
    return (linearForwardMs2 / 9.80665f).coerceIn(-1.5f, 1.5f)
  }

  /**
   * Checks whether the acceleration event is a true sudden brake or a speed bump / pothole spike.
   * @param forwardG G-force along forward travel axis (negative for braking)
   * @param verticalG G-force along vertical Z axis (large for potholes/bumps)
   * @param currentTimeMs Current timestamp
   */
  fun evaluateBrakeEvent(forwardG: Float, verticalG: Float, currentTimeMs: Long): Boolean {
    // 1. If vertical shock is unusually high compared to forward deceleration, it's a speed bump or pothole
    if (abs(verticalG) > 0.8f && abs(forwardG) < 0.5f) {
      brakeStartTime = 0L
      return false
    }

    // 2. Check if forward deceleration exceeds threshold
    if (forwardG <= BRAKE_G_THRESHOLD) {
      if (brakeStartTime == 0L) {
        brakeStartTime = currentTimeMs
      }
      val duration = currentTimeMs - brakeStartTime
      // Must sustain for at least 500ms to be classified as genuine emergency braking
      if (duration >= MIN_EVENT_DURATION_MS) {
        brakeStartTime = 0L // Reset after confirming event
        lastHardBrakeTimestampMs = currentTimeMs // Record for road rage chain reaction detection
        return true
      }
    } else {
      brakeStartTime = 0L
    }
    return false
  }

  /**
   * Checks whether the acceleration event is a genuine rapid acceleration.
   */
  fun evaluateAccelEvent(forwardG: Float, currentTimeMs: Long): Boolean {
    if (forwardG >= ACCEL_G_THRESHOLD) {
      if (accelStartTime == 0L) {
        accelStartTime = currentTimeMs
      }
      val duration = currentTimeMs - accelStartTime
      if (duration >= MIN_EVENT_DURATION_MS) {
        accelStartTime = 0L
        return true
      }
    } else {
      accelStartTime = 0L
    }
    return false
  }

  /**
   * Evaluates if a sudden acceleration immediately follows a hard brake (within 4s),
   * indicative of an aggressive pursuit or road-rage escalation.
   */
  fun evaluateRoadRageRisk(forwardG: Float, currentTimeMs: Long): Boolean {
    if (lastHardBrakeTimestampMs > 0L && (currentTimeMs - lastHardBrakeTimestampMs) in 100L..4000L) {
      if (forwardG >= ACCEL_G_THRESHOLD) {
        lastHardBrakeTimestampMs = 0L // Reset to prevent double triggering
        return true
      }
    }
    return false
  }

  /**
   * Filters raw GPS speed to prevent stationary GPS drift and reject multipath reflections.
   * @param rawSpeedKmh Raw speed reported by GPS provider
   * @param gpsAccuracyMeters Accuracy radius in meters
   * @return Clamped and validated speed in km/h
   */
  fun filterGpsSpeed(rawSpeedKmh: Float, gpsAccuracyMeters: Float): Float {
    // Discard low-confidence GPS readings in urban tunnels or bad weather
    if (gpsAccuracyMeters > MAX_ACCEPTABLE_GPS_ACCURACY_METERS) {
      return 0f
    }
    // Zero-speed clamp: stationary drift prevention
    if (rawSpeedKmh < ZERO_SPEED_THRESHOLD_KMH) {
      return 0f
    }
    return rawSpeedKmh
  }
}
