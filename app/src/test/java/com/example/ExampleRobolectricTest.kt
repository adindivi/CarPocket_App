package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.DrivingHabitTrackerData
import com.example.viewmodel.SafeCoachViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("카포켓 (CarPocket)", appName)
  }

  @Test
  fun `tts service triggers alert when braking or acceleration score is low`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = SafeCoachViewModel(application)

    // Trigger low braking score (72 < 80 threshold)
    viewModel.triggerLowBrakingHabitAlert(72)
    val stateAfterBraking = viewModel.cockpitState.value
    assertEquals(72, stateAfterBraking.habitTracker.smoothBrakingScore)
    assertNotNull(stateAfterBraking.lowHabitAlertMessage)
    assertTrue(stateAfterBraking.lowHabitAlertMessage!!.contains("부드럽게 멈추기 점수가 72점"))
    assertEquals(stateAfterBraking.lowHabitAlertMessage, viewModel.audioCoach.lastSpokenAlert.value)

    // Trigger low acceleration score (74 < 80 threshold)
    viewModel.resetHabitScores()
    viewModel.triggerLowAccelerationHabitAlert(74)
    val stateAfterAccel = viewModel.cockpitState.value
    assertEquals(74, stateAfterAccel.habitTracker.smoothAccelerationScore)
    assertNotNull(stateAfterAccel.lowHabitAlertMessage)
    assertTrue(stateAfterAccel.lowHabitAlertMessage!!.contains("부드럽게 출발하기 점수가 74점"))
    assertEquals(stateAfterAccel.lowHabitAlertMessage, viewModel.audioCoach.lastSpokenAlert.value)

    // Test combined low braking and acceleration evaluation
    val combinedLowData = DrivingHabitTrackerData(smoothBrakingScore = 68, smoothAccelerationScore = 71)
    val combinedMsg = viewModel.audioCoach.evaluateAndSpeakLowHabitScore(combinedLowData, forceSpeak = true)
    assertNotNull(combinedMsg)
    assertTrue(combinedMsg!!.contains("멈추기(68점)"))
    assertTrue(combinedMsg.contains("출발하기(71점)"))
  }

  @Test
  fun `weekly driving report calculates summary score and updates 7 day trends`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = SafeCoachViewModel(application)
    viewModel.resetHabitScores()

    val records = viewModel.weeklyHabitRecords.value
    assertEquals(7, records.size)

    val summary = viewModel.weeklyReportSummary.value
    assertTrue(summary.overallSummaryScore in 1..100)
    assertTrue(summary.avgSmoothBraking in 1..100)
    assertTrue(summary.avgSmoothAcceleration in 1..100)
    assertTrue(summary.avgIndicatorUsage in 1..100)
    assertNotNull(summary.bestDayRecord)
    assertNotNull(summary.lowestDayRecord)

    // Simulate habit score update and verify today's record in the 7-day history reflects it
    viewModel.triggerLowBrakingHabitAlert(70)
    val updatedToday = viewModel.weeklyHabitRecords.value.last()
    assertEquals(70, updatedToday.smoothBrakingScore)
  }

  @Test
  fun `cctv enforcement map cameras contain dummy coordinates and update selection`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = SafeCoachViewModel(application)

    val cameras = viewModel.cctvCameras.value
    assertTrue(cameras.size >= 4)
    cameras.forEach { cam ->
      assertTrue(cam.latitude in 37.4900..37.5100)
      assertTrue(cam.longitude in 127.0200..127.0500)
      assertTrue(cam.coordinateText.contains("°N"))
    }

    val targetCam = cameras[2]
    viewModel.selectCctvCamera(targetCam, speakAlert = true)
    assertEquals(targetCam.id, viewModel.cockpitState.value.nearestCctv?.id)
  }
}


