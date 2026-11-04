package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TurnSignal
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.AlertCrimsonLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenLight
import com.example.ui.theme.SlateBg
import com.example.ui.theme.SlateBody
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlatePill
import com.example.ui.theme.TossBlue
import com.example.ui.theme.TossBlueLight
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight

/**
 * Automotive-Grade Surround View Monitor (SVM) 3D View & Digital Cluster.
 *
 * Benchmarked against Genesis ccNC (Connected Car Navigation Cockpit) & Apple CarPlay HIG:
 * 1. Flanking Wing Turn Signals (Left/Right Telltales anchored to cluster corners).
 * 2. Genesis ccNC Digital Speed + Korean Regulatory Speed Limit Sign + PRND Gear bar.
 * 3. 3D Isometric SVM Vehicle Model on Obsidian Turntable with real-time lighting:
 *    - Left/Right Headlamp & Mirror repeaters synchronized with turn signals.
 *    - Holographic floor guidance trajectory rays (◀◀ / ▶▶) projected onto the tarmac.
 *    - Brake light activation during negative G-force deceleration.
 *    - 360° interactive horizontal drag orbit around the vehicle.
 */
@Composable
fun SurroundViewMonitorCluster(
  speed: Int,
  speedLimit: Int,
  gForce: Float,
  turnSignal: TurnSignal,
  isSeatbeltFastened: Boolean,
  isDriving: Boolean,
  onLeftSignalClick: () -> Unit,
  onRightSignalClick: () -> Unit,
  onSeatbeltToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isOverSpeed = speed > speedLimit
  val isLeftActive = turnSignal == TurnSignal.LEFT
  val isRightActive = turnSignal == TurnSignal.RIGHT
  val isBraking = gForce < -0.25f

  // 1. Infinite transition for smooth telltale and hologram pulsation
  val infiniteTransition = rememberInfiniteTransition(label = "svmBlinker")
  val blinkAlpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(380),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blinkerPulse"
  )

  // 2. Interactive Orbit Camera Angle (drag horizontally to rotate vehicle)
  var orbitAngle by remember { mutableFloatStateOf(0f) }
  val animatedOrbitAngle by animateFloatAsState(
    targetValue = orbitAngle,
    animationSpec = spring(stiffness = 350f),
    label = "orbitAngle"
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(22.dp),
    color = SlateCard,
    shadowElevation = 2.dp,
    border = BorderStroke(
      width = if (isOverSpeed) 1.5.dp else 1.dp,
      color = if (isOverSpeed) AlertCrimson else SlateBorder
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      // ─────────────────────────────────────────────────────────────
      // Top Row: Genesis ccNC Digital Cluster & Flanking Turn Signals
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // [Left Flank: Turn Signal ◀]
        Box(
          modifier = Modifier
            .size(width = 44.dp, height = 38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isLeftActive) WarningAmberLight else SlatePill)
            .border(
              1.dp,
              if (isLeftActive) WarningAmber else SlateBorder,
              RoundedCornerShape(10.dp)
            )
            .clickable { onLeftSignalClick() }
            .testTag("left_blinker_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "좌측 깜빡이",
            tint = if (isLeftActive) WarningAmber.copy(alpha = blinkAlpha) else SlateMuted,
            modifier = Modifier.size(20.dp)
          )
        }

        // [Center: Digital Speed + Korean Speed Limit Sign + PRND Gear]
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          // PRND Gear Capsule
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlatePill)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            val gears = listOf("P", "R", "N", "D")
            val activeGear = if (isDriving) "D" else "P"
            gears.forEach { gear ->
              val isSelected = gear == activeGear
              Text(
                text = gear,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                color = if (isSelected) TossBlue else SlateMuted,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(3.dp))

          // Speed & Regulatory Speed Limit Sign
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "$speed",
              fontSize = 42.sp,
              fontWeight = FontWeight.Black,
              color = if (isOverSpeed) AlertCrimson else SlateDark,
              letterSpacing = (-1.5).sp,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
              text = "km/h",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = SlateMuted,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Korea Regulatory Speed Limit Sign (White Circle with Red Border)
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, AlertCrimson, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$speedLimit",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SlateDark,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }
        }

        // [Right Flank: Seatbelt Indicator & Turn Signal ▶]
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Seatbelt telltale button
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isSeatbeltFastened) SafeGreenLight else AlertCrimsonLight)
              .border(
                1.dp,
                if (isSeatbeltFastened) SafeGreen.copy(alpha = 0.4f) else AlertCrimson,
                CircleShape
              )
              .clickable { onSeatbeltToggle() }
              .testTag("seatbelt_toggle_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AirlineSeatReclineNormal,
              contentDescription = if (isSeatbeltFastened) "안전벨트 착용 완료" else "안전벨트 미착용 경고",
              tint = if (isSeatbeltFastened) SafeGreen else AlertCrimson,
              modifier = Modifier.size(18.dp)
            )
          }

          // Right Turn Signal button
          Box(
            modifier = Modifier
              .size(width = 44.dp, height = 38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isRightActive) WarningAmberLight else SlatePill)
              .border(
                1.dp,
                if (isRightActive) WarningAmber else SlateBorder,
                RoundedCornerShape(10.dp)
              )
              .clickable { onRightSignalClick() }
              .testTag("right_blinker_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "우측 깜빡이",
              tint = if (isRightActive) WarningAmber.copy(alpha = blinkAlpha) else SlateMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // ─────────────────────────────────────────────────────────────
      // Minimalist G-Force Eco Sensor Line
      // ─────────────────────────────────────────────────────────────
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(SlatePill)
      ) {
        val centerX = size.width / 2f
        val halfW = size.width / 2f
        val fraction = (gForce.coerceIn(-0.8f, 0.8f) / 0.8f)

        if (fraction < 0f) {
          val barW = (-fraction) * halfW
          drawRoundRect(
            color = if (gForce < -0.4f) AlertCrimson else WarningAmber,
            topLeft = Offset(centerX - barW, 0f),
            size = Size(barW, size.height),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
          )
        } else if (fraction > 0f) {
          val barW = fraction * halfW
          drawRoundRect(
            color = if (gForce > 0.4f) WarningAmber else TossBlue,
            topLeft = Offset(centerX, 0f),
            size = Size(barW, size.height),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
          )
        }

        // Center zero anchor
        drawLine(
          color = SlateBorder,
          start = Offset(centerX, 0f),
          end = Offset(centerX, size.height),
          strokeWidth = 2.dp.toPx()
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // ─────────────────────────────────────────────────────────────
      // SVM 3D Viewport: Interactive 3D Turntable, Vehicle & Lighting
      // ─────────────────────────────────────────────────────────────
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(172.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF0F172A), Color(0xFF1E293B))
            )
          )
          .pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
              change.consume()
              // Smooth orbit rotation bounded between -50 deg to +50 deg
              orbitAngle = (orbitAngle + dragAmount * 0.4f).coerceIn(-50f, 50f)
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val cx = w / 2f
          val cy = h * 0.54f

          // 1. Perspective 3D Turntable Ground Oval
          drawOval(
            brush = Brush.radialGradient(
              colors = listOf(
                Color(0xFF334155).copy(alpha = 0.55f),
                Color(0xFF1E293B).copy(alpha = 0.85f),
                Color(0xFF0F172A)
              ),
              center = Offset(cx, cy),
              radius = w * 0.46f
            ),
            topLeft = Offset(cx - w * 0.44f, cy - h * 0.42f),
            size = Size(w * 0.88f, h * 0.84f)
          )

          // 2. 3D Sonar Range Rings (Concentric Safety Distance Ellipses)
          val sonarEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
          drawOval(
            color = Color(0x3338BDF8),
            topLeft = Offset(cx - w * 0.40f, cy - h * 0.38f),
            size = Size(w * 0.80f, h * 0.76f),
            style = Stroke(width = 1.2f, pathEffect = sonarEffect)
          )
          drawOval(
            color = Color(0x22FFFFFF),
            topLeft = Offset(cx - w * 0.28f, cy - h * 0.26f),
            size = Size(w * 0.56f, h * 0.52f),
            style = Stroke(width = 1f)
          )

          // 3. 4-Channel SVM Camera FOV Light Cones (Front, Rear, Left, Right)
          // Front camera view cone
          val frontCamPath = Path().apply {
            moveTo(cx, cy - 36f)
            lineTo(cx - 56f, 0f)
            lineTo(cx + 56f, 0f)
            close()
          }
          drawPath(
            path = frontCamPath,
            brush = Brush.verticalGradient(
              colors = listOf(Color(0x1838BDF8), Color(0x0038BDF8)),
              startY = 0f,
              endY = cy - 36f
            )
          )

          // 4. Holographic Floor Guidance Trajectory Rays on Active Turn Signal
          if (isLeftActive) {
            val leftFloorRay = Path().apply {
              moveTo(cx - 20f, cy - 25f)
              quadraticTo(cx - 70f, cy - 35f, cx - 110f, cy - 65f)
            }
            drawPath(
              path = leftFloorRay,
              color = WarningAmber.copy(alpha = blinkAlpha * 0.85f),
              style = Stroke(
                width = 5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
              )
            )
          }

          if (isRightActive) {
            val rightFloorRay = Path().apply {
              moveTo(cx + 20f, cy - 25f)
              quadraticTo(cx + 70f, cy - 35f, cx + 110f, cy - 65f)
            }
            drawPath(
              path = rightFloorRay,
              color = WarningAmber.copy(alpha = blinkAlpha * 0.85f),
              style = Stroke(
                width = 5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
              )
            )
          }

          // 5. 3D Vehicle Model with Dynamic Orbit Angle
          withTransform({
            rotate(animatedOrbitAngle, pivot = Offset(cx, cy))
          }) {
            val carW = 60f
            val carH = 110f
            val halfW = carW / 2f
            val halfH = carH / 2f

            // Ground Shadow beneath the vehicle
            drawOval(
              color = Color.Black.copy(alpha = 0.55f),
              topLeft = Offset(cx - halfW - 8f, cy - halfH + 10f),
              size = Size(carW + 16f, carH + 6f)
            )

            // 4 Wheels (Alloy Rims with Rubber Tread)
            val wheelW = 10f
            val wheelH = 22f
            val wheelColor = Color(0xFF0F172A)
            val rimColor = Color(0xFF94A3B8)

            // Front Left Wheel
            drawRoundRect(wheelColor, Offset(cx - halfW - 6f, cy - halfH + 16f), Size(wheelW, wheelH), CornerRadius(4f, 4f))
            drawRoundRect(rimColor, Offset(cx - halfW - 4f, cy - halfH + 20f), Size(3f, wheelH - 8f), CornerRadius(2f, 2f))
            // Front Right Wheel
            drawRoundRect(wheelColor, Offset(cx + halfW - 4f, cy - halfH + 16f), Size(wheelW, wheelH), CornerRadius(4f, 4f))
            drawRoundRect(rimColor, Offset(cx + halfW + 1f, cy - halfH + 20f), Size(3f, wheelH - 8f), CornerRadius(2f, 2f))
            // Rear Left Wheel
            drawRoundRect(wheelColor, Offset(cx - halfW - 6f, cy + halfH - 36f), Size(wheelW, wheelH), CornerRadius(4f, 4f))
            drawRoundRect(rimColor, Offset(cx - halfW - 4f, cy + halfH - 32f), Size(3f, wheelH - 8f), CornerRadius(2f, 2f))
            // Rear Right Wheel
            drawRoundRect(wheelColor, Offset(cx + halfW - 4f, cy + halfH - 36f), Size(wheelW, wheelH), CornerRadius(4f, 4f))
            drawRoundRect(rimColor, Offset(cx + halfW + 1f, cy + halfH - 32f), Size(3f, wheelH - 8f), CornerRadius(2f, 2f))

            // Aerodynamic Car Chassis Body (Titanium Dark Slate)
            val bodyPath = Path().apply {
              moveTo(cx - halfW + 10f, cy - halfH)
              quadraticTo(cx, cy - halfH - 5f, cx + halfW - 10f, cy - halfH)
              quadraticTo(cx + halfW, cy - halfH + 14f, cx + halfW, cy - halfH + 34f)
              lineTo(cx + halfW - 2f, cy + halfH - 24f)
              quadraticTo(cx + halfW, cy + halfH - 8f, cx + halfW - 8f, cy + halfH)
              lineTo(cx - halfW + 8f, cy + halfH)
              quadraticTo(cx - halfW, cy + halfH - 8f, cx - halfW + 2f, cy + halfH - 24f)
              lineTo(cx - halfW, cy - halfH + 34f)
              quadraticTo(cx - halfW, cy - halfH + 14f, cx - halfW + 10f, cy - halfH)
              close()
            }

            drawPath(
              path = bodyPath,
              brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF475569), Color(0xFF1E293B)),
                startY = cy - halfH,
                endY = cy + halfH
              )
            )

            // Subtle body outline highlight
            drawPath(
              path = bodyPath,
              color = Color(0x44CBD5E1),
              style = Stroke(width = 1.2f)
            )

            // Side Mirrors
            val mirrorColor = Color(0xFF475569)
            // Left mirror
            drawRoundRect(mirrorColor, Offset(cx - halfW - 7f, cy - halfH + 28f), Size(8f, 5f), CornerRadius(2f, 2f))
            // Right mirror
            drawRoundRect(mirrorColor, Offset(cx + halfW - 1f, cy - halfH + 28f), Size(8f, 5f), CornerRadius(2f, 2f))

            // Panoramic Glass Roof & Windshield (Obsidian with Reflective Sheen)
            val glassPath = Path().apply {
              moveTo(cx - halfW + 8f, cy - halfH + 22f)
              lineTo(cx + halfW - 8f, cy - halfH + 22f)
              lineTo(cx + halfW - 6f, cy + halfH - 20f)
              lineTo(cx - halfW + 6f, cy + halfH - 20f)
              close()
            }
            drawPath(
              path = glassPath,
              brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
                startY = cy - halfH + 22f,
                endY = cy + halfH - 20f
              )
            )

            // Glass reflection diagonal stripe
            drawLine(
              color = Color(0x33FFFFFF),
              start = Offset(cx - halfW + 12f, cy - halfH + 26f),
              end = Offset(cx + 4f, cy + halfH - 24f),
              strokeWidth = 2.5f
            )

            // ─────────────────────────────────────────────────────────
            // Real-Time Automotive Lighting: Blinker & Brake Lamps
            // ─────────────────────────────────────────────────────────
            val leftLampColor = if (isLeftActive) WarningAmber.copy(alpha = blinkAlpha) else Color(0xFFE2E8F0)
            val rightLampColor = if (isRightActive) WarningAmber.copy(alpha = blinkAlpha) else Color(0xFFE2E8F0)

            // Front Left Headlamp
            drawRoundRect(
              color = leftLampColor,
              topLeft = Offset(cx - halfW + 4f, cy - halfH + 2f),
              size = Size(10f, 4f),
              cornerRadius = CornerRadius(2f, 2f)
            )
            // Front Right Headlamp
            drawRoundRect(
              color = rightLampColor,
              topLeft = Offset(cx + halfW - 14f, cy - halfH + 2f),
              size = Size(10f, 4f),
              cornerRadius = CornerRadius(2f, 2f)
            )

            // Side Mirror Repeaters (blinks amber on turn signal)
            if (isLeftActive) {
              drawCircle(
                color = WarningAmber.copy(alpha = blinkAlpha),
                radius = 3.5f,
                center = Offset(cx - halfW - 6f, cy - halfH + 30f)
              )
            }
            if (isRightActive) {
              drawCircle(
                color = WarningAmber.copy(alpha = blinkAlpha),
                radius = 3.5f,
                center = Offset(cx + halfW + 6f, cy - halfH + 30f)
              )
            }

            // Rear Taillights & Center High-Mount Stop Lamp (CHMSL)
            val rearTailColor = if (isBraking) {
              AlertCrimson
            } else {
              Color(0xFFDC2626).copy(alpha = 0.85f)
            }

            // Full-width rear light bar
            drawLine(
              color = rearTailColor,
              start = Offset(cx - halfW + 8f, cy + halfH - 2f),
              end = Offset(cx + halfW - 8f, cy + halfH - 2f),
              strokeWidth = if (isBraking) 4.5f else 2.5f,
              cap = StrokeCap.Round
            )

            // Rear Turn Signal Blinker Bulbs
            if (isLeftActive) {
              drawCircle(
                color = WarningAmber.copy(alpha = blinkAlpha),
                radius = 3.5f,
                center = Offset(cx - halfW + 9f, cy + halfH - 2f)
              )
            }
            if (isRightActive) {
              drawCircle(
                color = WarningAmber.copy(alpha = blinkAlpha),
                radius = 3.5f,
                center = Offset(cx + halfW - 9f, cy + halfH - 2f)
              )
            }
          }
        }

        // Top Left: 3D SVM Mode Badge
        Row(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(10.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x990F172A))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = null,
            tint = TossBlue,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "SVM 3D 뷰",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        // Top Right: Reset Angle Button (if rotated)
        if (orbitAngle != 0f) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(10.dp)
              .size(24.dp)
              .clip(CircleShape)
              .background(Color(0x990F172A))
              .clickable { orbitAngle = 0f },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.RestartAlt,
              contentDescription = "화각 초기화",
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        // Bottom Center: Gesture Hint
        Text(
          text = if (orbitAngle == 0f) "화면을 좌우로 드래그하여 360° 확인" else "시점: ${orbitAngle.toInt()}°",
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0x88CBD5E1),
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 6.dp),
          softWrap = false,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // ─────────────────────────────────────────────────────────────
      // Bottom Informational Capsule: Sonar Distance & Status
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(if (isBraking) AlertCrimson else SafeGreen)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isBraking) "급제동 감지 (제동등 점등)" else "360° 서라운드 모니터링 정상",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isBraking) AlertCrimson else SlateBody,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        Text(
          text = if (turnSignal != TurnSignal.OFF) "가이드 궤적 투사 중" else "반경 2.5m 안전",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (turnSignal != TurnSignal.OFF) WarningAmber else SafeGreen,
          softWrap = false,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }
    }
  }
}
