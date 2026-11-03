package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.DrivingHabitTrackerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

class AudioCoachManager(private val context: Context) : TextToSpeech.OnInitListener {

  companion object {
    const val LOW_HABIT_SCORE_THRESHOLD = 80
    const val CRITICAL_HABIT_SCORE_THRESHOLD = 70
  }

  private var tts: TextToSpeech? = null
  private var isInitialized = false
  private val pendingSpeechQueue = ConcurrentLinkedQueue<String>()
  var isVoiceEnabled: Boolean = true

  /**
   * If true, suppresses repetitive verbose TTS during driving and replaces them with clean chime beeps,
   * saving comprehensive voice feedback for the post-trip Quick Debrief.
   */
  var isBeepOnlyMode: Boolean = true

  private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
  private var audioFocusRequest: AudioFocusRequest? = null
  private var toneGenerator: ToneGenerator? = try {
    ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
  } catch (_: Exception) {
    null
  }

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _lastSpokenAlert = MutableStateFlow<String?>(null)
  val lastSpokenAlert: StateFlow<String?> = _lastSpokenAlert.asStateFlow()

  // Tracks last alerted score bands (0 = normal >=80, 1 = warning 70..79, 2 = critical <70)
  private var lastBrakingAlertBand = 0
  private var lastAccelAlertBand = 0

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  init {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (e: Exception) {
      Log.e("AudioCoachManager", "Failed to instantiate TTS", e)
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale.KOREAN)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        tts?.setLanguage(Locale.getDefault())
      }
      tts?.setSpeechRate(1.04f)
      tts?.setPitch(1.0f)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val audioAttributes = AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
          .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
          .build()
        tts?.setAudioAttributes(audioAttributes)
      }

      isInitialized = true

      tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isSpeaking.value = true
        }

        override fun onDone(utteranceId: String?) {
          _isSpeaking.value = false
          abandonAudioFocus()
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
          _isSpeaking.value = false
          abandonAudioFocus()
        }
      })

      while (pendingSpeechQueue.isNotEmpty()) {
        pendingSpeechQueue.poll()?.let { queued ->
          speak(queued, vibrate = false)
        }
      }
    } else {
      Log.e("AudioCoachManager", "TTS initialization failed: $status")
    }
  }

  fun playBeepTone(toneType: Int = ToneGenerator.TONE_PROP_BEEP, durationMs: Int = 180) {
    try {
      toneGenerator?.startTone(toneType, durationMs)
    } catch (_: Exception) {
    }
  }

  fun speak(
    text: String,
    vibrate: Boolean = true,
    queueMode: Int = TextToSpeech.QUEUE_FLUSH,
    isHighPriority: Boolean = false
  ) {
    _lastSpokenAlert.value = text
    if (vibrate) {
      triggerHapticAlert()
    }

    // In beep-only mode during driving, play a quick non-intrusive chime tone instead of noisy TTS sentences,
    // unless this is a high-priority message (e.g. 30s Quick Debrief or Road Rage Calm Intervention).
    if (isBeepOnlyMode && !isHighPriority) {
      playBeepTone()
      return
    }

    if (!isVoiceEnabled) return
    if (!isInitialized) {
      pendingSpeechQueue.add(text)
      return
    }
    try {
      requestAudioFocus()
      // Play a short 120ms lead to warm up vehicle bluetooth A2DP audio channel
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        tts?.playSilentUtterance(120, TextToSpeech.QUEUE_FLUSH, "bt_warmup")
      }
      val utteranceId = "safecoach_${System.currentTimeMillis()}"
      tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
    } catch (e: Exception) {
      abandonAudioFocus()
      Log.e("AudioCoachManager", "Error speaking: $text", e)
    }
  }

  /**
   * Specifically speaks a high-priority narrative (e.g. 30s Quick Debrief or Road Rage De-escalation),
   * bypassing the beep-only suppression filter.
   */
  fun speakBriefing(text: String, vibrate: Boolean = false) {
    speak(text, vibrate = vibrate, queueMode = TextToSpeech.QUEUE_FLUSH, isHighPriority = true)
  }

  private fun requestAudioFocus() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (audioFocusRequest == null) {
          val playbackAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
          audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(playbackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { /* Handle ducking if needed */ }
            .build()
        }
        audioFocusRequest?.let { audioManager?.requestAudioFocus(it) }
      } else {
        @Suppress("DEPRECATION")
        audioManager?.requestAudioFocus(
          null,
          AudioManager.STREAM_NOTIFICATION,
          AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        )
      }
    } catch (e: Exception) {
      Log.e("AudioCoachManager", "Failed to request audio focus", e)
    }
  }

  private fun abandonAudioFocus() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
      } else {
        @Suppress("DEPRECATION")
        audioManager?.abandonAudioFocus(null)
      }
    } catch (e: Exception) {
      Log.e("AudioCoachManager", "Failed to abandon audio focus", e)
    }
  }

  /**
   * Evaluates the DrivingHabitTrackerData and automatically triggers a TTS audio alert
   * when a low score (< threshold, default 80) is detected in Smooth Braking or Smooth Acceleration.
   */
  fun evaluateAndSpeakLowHabitScore(
    habitData: DrivingHabitTrackerData,
    threshold: Int = LOW_HABIT_SCORE_THRESHOLD,
    forceSpeak: Boolean = false
  ): String? {
    val brakingScore = habitData.smoothBrakingScore
    val accelScore = habitData.smoothAccelerationScore

    val currentBrakingBand = getScoreBand(brakingScore, threshold)
    val currentAccelBand = getScoreBand(accelScore, threshold)

    val brakingDropped = currentBrakingBand > lastBrakingAlertBand
    val accelDropped = currentAccelBand > lastAccelAlertBand

    lastBrakingAlertBand = currentBrakingBand
    lastAccelAlertBand = currentAccelBand

    val isBrakingLow = brakingScore < threshold
    val isAccelLow = accelScore < threshold

    if (!isBrakingLow && !isAccelLow) {
      return null
    }

    if (!forceSpeak && !brakingDropped && !accelDropped) {
      return buildHabitLowScoreMessage(brakingScore, accelScore, threshold)
    }

    return when {
      isBrakingLow && isAccelLow -> speakCombinedLowHabitScoreAlert(brakingScore, accelScore, threshold)
      isBrakingLow -> speakLowBrakingScoreAlert(brakingScore, threshold)
      isAccelLow -> speakLowAccelerationScoreAlert(accelScore, threshold)
      else -> null
    }
  }

  fun speakLowBrakingScoreAlert(
    score: Int,
    threshold: Int = LOW_HABIT_SCORE_THRESHOLD
  ): String {
    val severityPrefix = if (score < CRITICAL_HABIT_SCORE_THRESHOLD) "주의해 주세요" else "안전운전 알림"
    val message =
      "$severityPrefix: 부드럽게 멈추기 점수가 ${score}점이에요. 앞차와 3초 안전거리를 두면 부드럽게 멈출 수 있어요."
    triggerWarningPatternHaptic()
    speak(message, vibrate = false, queueMode = TextToSpeech.QUEUE_FLUSH)
    return message
  }

  fun speakLowAccelerationScoreAlert(
    score: Int,
    threshold: Int = LOW_HABIT_SCORE_THRESHOLD
  ): String {
    val severityPrefix = if (score < CRITICAL_HABIT_SCORE_THRESHOLD) "주의해 주세요" else "안전운전 알림"
    val message =
      "$severityPrefix: 부드럽게 출발하기 점수가 ${score}점이에요. 출발할 때 페달을 천천히 밟으면 점수도 오르고 기름값도 아껴요."
    triggerWarningPatternHaptic()
    speak(message, vibrate = false, queueMode = TextToSpeech.QUEUE_FLUSH)
    return message
  }

  fun speakCombinedLowHabitScoreAlert(
    brakingScore: Int,
    accelScore: Int,
    threshold: Int = LOW_HABIT_SCORE_THRESHOLD
  ): String {
    val message =
      "안전운전 알림: 멈추기(${brakingScore}점)와 출발하기(${accelScore}점) 점수가 조금 낮아졌어요. 보험료 할인을 위해 부드럽게 운전해 보세요."
    triggerWarningPatternHaptic()
    speak(message, vibrate = false, queueMode = TextToSpeech.QUEUE_FLUSH)
    return message
  }

  private fun buildHabitLowScoreMessage(
    brakingScore: Int,
    accelScore: Int,
    threshold: Int
  ): String {
    return when {
      brakingScore < threshold && accelScore < threshold ->
        "멈추기(${brakingScore}점), 출발하기(${accelScore}점) 점수가 80점 아래로 내려갔어요."
      brakingScore < threshold ->
        "부드럽게 멈추기 점수가 ${brakingScore}점으로 80점 아래로 내려갔어요."
      else ->
        "부드럽게 출발하기 점수가 ${accelScore}점으로 80점 아래로 내려갔어요."
    }
  }

  private fun getScoreBand(score: Int, threshold: Int): Int {
    return when {
      score < CRITICAL_HABIT_SCORE_THRESHOLD -> 2
      score < threshold -> 1
      else -> 0
    }
  }

  fun resetAlertBands() {
    lastBrakingAlertBand = 0
    lastAccelAlertBand = 0
  }

  fun stop() {
    try {
      tts?.stop()
      _isSpeaking.value = false
    } catch (e: Exception) {
      Log.e("AudioCoachManager", "Error stopping TTS", e)
    }
  }

  fun triggerHapticAlert() {
    try {
      if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(
            VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
          )
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(200)
        }
      }
    } catch (e: Exception) {
      Log.w("AudioCoachManager", "Vibrate error: ${e.message}")
    }
  }

  fun triggerWarningPatternHaptic() {
    try {
      if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 180, 100, 220), -1)
          )
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(350)
        }
      }
    } catch (e: Exception) {
      Log.w("AudioCoachManager", "Warning vibrate error: ${e.message}")
    }
  }

  fun shutdown() {
    try {
      toneGenerator?.release()
      toneGenerator = null
      tts?.stop()
      tts?.shutdown()
      tts = null
      _isSpeaking.value = false
    } catch (e: Exception) {
      Log.e("AudioCoachManager", "Error shutting down TTS", e)
    }
  }
}
