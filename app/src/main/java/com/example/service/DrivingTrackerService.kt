package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class DrivingTrackerService : Service() {

  companion object {
    const val CHANNEL_ID = "safecoach_driving_channel"
    const val NOTIFICATION_ID = 1001

    const val ACTION_START_DRIVING = "com.example.safecoach.action.START_DRIVING"
    const val ACTION_STOP_DRIVING = "com.example.safecoach.action.STOP_DRIVING"
    const val ACTION_UPDATE_STATS = "com.example.safecoach.action.UPDATE_STATS"

    const val EXTRA_SPEED = "extra_speed"
    const val EXTRA_SCORE = "extra_score"
    const val EXTRA_DISTANCE = "extra_distance"

    @Volatile
    var isServiceRunning: Boolean = false
      private set

    fun startService(context: Context, speed: Int = 0, score: Int = 100, distanceKm: Float = 0f) {
      val intent = Intent(context, DrivingTrackerService::class.java).apply {
        action = ACTION_START_DRIVING
        putExtra(EXTRA_SPEED, speed)
        putExtra(EXTRA_SCORE, score)
        putExtra(EXTRA_DISTANCE, distanceKm)
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopService(context: Context) {
      val intent = Intent(context, DrivingTrackerService::class.java).apply {
        action = ACTION_STOP_DRIVING
      }
      context.startService(intent)
    }

    fun updateStats(context: Context, speed: Int, score: Int, distanceKm: Float) {
      if (!isServiceRunning) return
      val intent = Intent(context, DrivingTrackerService::class.java).apply {
        action = ACTION_UPDATE_STATS
        putExtra(EXTRA_SPEED, speed)
        putExtra(EXTRA_SCORE, score)
        putExtra(EXTRA_DISTANCE, distanceKm)
      }
      context.startService(intent)
    }
  }

  private var wakeLock: PowerManager.WakeLock? = null
  private var currentSpeed: Int = 0
  private var currentScore: Int = 100
  private var currentDistance: Float = 0f

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    acquireWakeLock()
    isServiceRunning = true
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP_DRIVING -> {
        stopDrivingInternal()
        return START_NOT_STICKY
      }
      ACTION_UPDATE_STATS -> {
        currentSpeed = intent.getIntExtra(EXTRA_SPEED, currentSpeed)
        currentScore = intent.getIntExtra(EXTRA_SCORE, currentScore)
        currentDistance = intent.getFloatExtra(EXTRA_DISTANCE, currentDistance)
        updateNotification()
        return START_STICKY
      }
      ACTION_START_DRIVING, null -> {
        currentSpeed = intent?.getIntExtra(EXTRA_SPEED, 0) ?: 0
        currentScore = intent?.getIntExtra(EXTRA_SCORE, 100) ?: 100
        currentDistance = intent?.getFloatExtra(EXTRA_DISTANCE, 0f) ?: 0f

        val notification = buildNotification()
        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasLocationPerm = ContextCompat.checkSelfPermission(
              this,
              android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
              this,
              android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasLocationPerm) {
              startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
              )
            } else {
              startForeground(NOTIFICATION_ID, notification)
            }
          } else {
            startForeground(NOTIFICATION_ID, notification)
          }
        } catch (e: SecurityException) {
          Log.w("DrivingTrackerService", "Location FGS security check fallback: ${e.message}")
          try {
            startForeground(NOTIFICATION_ID, notification)
          } catch (e2: Exception) {
            Log.e("DrivingTrackerService", "Failed to start foreground", e2)
          }
        }
        return START_STICKY
      }
    }
    return START_STICKY
  }

  private fun stopDrivingInternal() {
    isServiceRunning = false
    releaseWakeLock()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      stopForeground(STOP_FOREGROUND_REMOVE)
    } else {
      @Suppress("DEPRECATION")
      stopForeground(true)
    }
    stopSelf()
  }

  private fun acquireWakeLock() {
    if (wakeLock == null) {
      val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
      wakeLock = pm?.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        "SafeCoach:DrivingTrackerWakeLock"
      )?.apply {
        setReferenceCounted(false)
        acquire(4 * 60 * 60 * 1000L) // Safety timeout: 4 hours
      }
    }
  }

  private fun releaseWakeLock() {
    wakeLock?.let {
      if (it.isHeld) {
        it.release()
      }
    }
    wakeLock = null
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "카포켓 (CarPocket) 주행 기록",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "실시간 안전운전 코칭 및 주행 기록이 백그라운드에서 유지됩니다."
        setShowBadge(false)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
      }
      val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }

  private fun buildNotification(): Notification {
    val openAppIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val contentPendingIntent = PendingIntent.getActivity(
      this,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val stopIntent = Intent(this, DrivingTrackerService::class.java).apply {
      action = ACTION_STOP_DRIVING
    }
    val stopPendingIntent = PendingIntent.getService(
      this,
      1,
      stopIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val subText = String.format("속도 %dkm/h · 운전점수 %d점 · %.1fkm 주행 중", currentSpeed, currentScore, currentDistance)

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("카포켓 (CarPocket) 안전운전 중")
      .setContentText(subText)
      .setSmallIcon(android.R.drawable.ic_menu_compass)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setContentIntent(contentPendingIntent)
      .addAction(android.R.drawable.ic_media_pause, "운전 마치기", stopPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_SERVICE)
      .build()
  }

  private fun updateNotification() {
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    manager?.notify(NOTIFICATION_ID, buildNotification())
  }

  override fun onDestroy() {
    isServiceRunning = false
    releaseWakeLock()
    super.onDestroy()
  }
}
