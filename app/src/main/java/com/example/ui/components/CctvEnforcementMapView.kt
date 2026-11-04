package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraType
import com.example.model.EnforcementCamera
import com.example.model.VehicleGpsCoordinate
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

/**
 * Interactive Map View with Pin Markers for nearby traffic enforcement cameras
 * using dummy GPS coordinates, simulating the '단속정보' (Traffic Enforcement Info) feature.
 */
@Composable
fun CctvEnforcementMapView(
  cameras: List<EnforcementCamera>,
  selectedCamera: EnforcementCamera?,
  vehicleCoordinate: VehicleGpsCoordinate,
  isStationary: Boolean,
  onSelectCamera: (EnforcementCamera) -> Unit,
  onOpenFullList: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var selectedTypeFilter by remember { mutableStateOf<CameraType?>(null) }
  var isZoomedIn by remember { mutableStateOf(false) }

  val visibleCameras = remember(cameras, selectedTypeFilter) {
    if (selectedTypeFilter == null) cameras
    else cameras.filter { it.type == selectedTypeFilter }
  }

  val activeCamera = remember(selectedCamera, visibleCameras) {
    if (selectedCamera != null && visibleCameras.any { it.id == selectedCamera.id }) {
      selectedCamera
    } else {
      visibleCameras.firstOrNull() ?: selectedCamera
    }
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("cctv_enforcement_map_card"),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = BorderStroke(
      width = 1.5.dp,
      color = if (isStationary) AmberWarning.copy(alpha = 0.55f) else LightCardBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Top Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(if (isStationary) AmberGlow else CyanGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "단속정보 지도",
              tint = if (isStationary) AmberWarning else CyanNeon,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "주변 주정차 단속 구역 & CCTV",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isStationary) AmberGlow else EmeraldGlow)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isStationary) "정차 단속 주의" else "주정차 레이더",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isStationary) AmberWarning else EmeraldSafe
                )
              }
            }
            Text(
              text = "${vehicleCoordinate.addressLabel} (${String.format("%.4f°N, %.4f°E", vehicleCoordinate.latitude, vehicleCoordinate.longitude)})",
              fontSize = 11.sp,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        if (onOpenFullList != null) {
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(LightSurfaceSubtle)
              .border(1.dp, LightCardBorder, RoundedCornerShape(10.dp))
              .clickable { onOpenFullList() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("open_cctv_modal_button")
          ) {
            Text(
              text = "목록보기 (${cameras.size})",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CyanNeon
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Camera Type Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          MapFilterChip(
            label = "전체 구역 (${cameras.size})",
            color = CyanNeon,
            isSelected = selectedTypeFilter == null,
            onClick = { selectedTypeFilter = null },
            tag = "map_filter_ALL"
          )
        }
        items(CameraType.values()) { type ->
          val count = cameras.count { it.type == type }
          MapFilterChip(
            label = "${type.shortLabel} ($count)",
            color = cameraTypeColor(type),
            isSelected = selectedTypeFilter == type,
            onClick = {
              selectedTypeFilter = if (selectedTypeFilter == type) null else type
            },
            tag = "map_filter_${type.name}"
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Interactive Map Canvas with Pin Markers at Dummy Coordinates
      CctvMapViewport(
        cameras = visibleCameras,
        selectedCamera = activeCamera,
        vehicleCoordinate = vehicleCoordinate,
        isStationary = isStationary,
        isZoomedIn = isZoomedIn,
        onToggleZoom = { isZoomedIn = !isZoomedIn },
        onPinClick = onSelectCamera
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Selected Pin Detail & Audio Alert Panel
      if (activeCamera != null) {
        SelectedCameraDetailPanel(
          camera = activeCamera,
          isStationary = isStationary,
          onSpeakAlert = { onSelectCamera(activeCamera) }
        )
      }
    }
  }
}

@Composable
private fun CctvMapViewport(
  cameras: List<EnforcementCamera>,
  selectedCamera: EnforcementCamera?,
  vehicleCoordinate: VehicleGpsCoordinate,
  isStationary: Boolean,
  isZoomedIn: Boolean,
  onToggleZoom: () -> Unit,
  onPinClick: (EnforcementCamera) -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.7f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseScale"
  )

  // Coordinate bounds around Gangnam/Yeoksam (37.5006, 127.0364)
  val latSpan = if (isZoomedIn) 0.0055 else 0.0078
  val lngSpan = if (isZoomedIn) 0.0075 else 0.0105
  val minLat = vehicleCoordinate.latitude - latSpan
  val maxLat = vehicleCoordinate.latitude + latSpan
  val minLng = vehicleCoordinate.longitude - lngSpan
  val maxLng = vehicleCoordinate.longitude + lngSpan

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxWidth()
      .height(228.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFFEEF4FA))
      .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
      .testTag("cctv_map_canvas_container")
  ) {
    val mapWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
    val mapHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

    // Helper function projecting (lat, lng) -> normalized [0.10 .. 0.90] so pins never clip
    fun projectX(lng: Double): Float {
      val norm = ((lng - minLng) / (maxLng - minLng)).toFloat().coerceIn(0.08f, 0.92f)
      return norm * mapWidthPx
    }

    fun projectY(lat: Double): Float {
      // Higher latitude is further North (top of canvas -> smaller Y)
      val norm = (1.0 - (lat - minLat) / (maxLat - minLat)).toFloat().coerceIn(0.12f, 0.88f)
      return norm * mapHeightPx
    }

    val vehicleX = projectX(vehicleCoordinate.longitude)
    val vehicleY = projectY(vehicleCoordinate.latitude)

    // Background Road Map & Radar Rings Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // 1. Subtle urban block grid
      val gridCols = 6
      val gridRows = 4
      val cellW = w / gridCols
      val cellH = h / gridRows
      for (r in 0 until gridRows) {
        for (c in 0 until gridCols) {
          val isGreenZone = (r == 0 && c == 4) || (r == 3 && c == 1)
          drawRoundRect(
            color = if (isGreenZone) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
            topLeft = Offset(c * cellW + 5f, r * cellH + 5f),
            size = Size(cellW - 10f, cellH - 10f),
            cornerRadius = CornerRadius(8f, 8f)
          )
        }
      }

      // 2. Major Arterial Roads (Gangnam-daero, Teheran-ro, Seolleung-ro, Olympic-daero)
      val roadCasingColor = Color(0xFFCBD5E1)
      val roadFillColor = Color(0xFFFFFFFF)
      val highwayFillColor = Color(0xFFFEF3C7)

      // Olympic-daero (Top East-West Expressway)
      val olympicY = h * 0.16f
      drawLine(
        color = roadCasingColor,
        start = Offset(0f, olympicY),
        end = Offset(w, olympicY * 0.85f),
        strokeWidth = 22f
      )
      drawLine(
        color = highwayFillColor,
        start = Offset(0f, olympicY),
        end = Offset(w, olympicY * 0.85f),
        strokeWidth = 17f
      )

      // Teheran-ro (Central SW-to-NE Main Avenue passing through vehicle)
      val teheranStart = Offset(0f, vehicleY + h * 0.16f)
      val teheranEnd = Offset(w, vehicleY - h * 0.18f)
      drawLine(
        color = roadCasingColor,
        start = teheranStart,
        end = teheranEnd,
        strokeWidth = 28f
      )
      drawLine(
        color = roadFillColor,
        start = teheranStart,
        end = teheranEnd,
        strokeWidth = 22f
      )
      drawLine(
        color = Color(0xFF94A3B8),
        start = teheranStart,
        end = teheranEnd,
        strokeWidth = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
      )

      // Gangnam-daero (West N-S Avenue)
      val gangnamX = w * 0.24f
      drawLine(
        color = roadCasingColor,
        start = Offset(gangnamX - 18f, 0f),
        end = Offset(gangnamX + 18f, h),
        strokeWidth = 24f
      )
      drawLine(
        color = roadFillColor,
        start = Offset(gangnamX - 18f, 0f),
        end = Offset(gangnamX + 18f, h),
        strokeWidth = 18f
      )

      // Seolleung-ro (East N-S Avenue)
      val seolleungX = w * 0.78f
      drawLine(
        color = roadCasingColor,
        start = Offset(seolleungX - 14f, 0f),
        end = Offset(seolleungX + 16f, h),
        strokeWidth = 22f
      )
      drawLine(
        color = roadFillColor,
        start = Offset(seolleungX - 14f, 0f),
        end = Offset(seolleungX + 16f, h),
        strokeWidth = 16f
      )

      // 3. Radar Range Circles centered on Vehicle GPS Position
      val maxRadius = w.coerceAtMost(h) * 0.44f
      drawCircle(
        color = CyanNeon.copy(alpha = 0.06f),
        radius = maxRadius,
        center = Offset(vehicleX, vehicleY)
      )
      drawCircle(
        color = CyanNeon.copy(alpha = 0.22f),
        radius = maxRadius,
        center = Offset(vehicleX, vehicleY),
        style = Stroke(
          width = 1.5f,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        )
      )
      drawCircle(
        color = CyanNeon.copy(alpha = 0.28f),
        radius = maxRadius * 0.55f,
        center = Offset(vehicleX, vehicleY),
        style = Stroke(width = 1.2f)
      )

      // 4. Active Route Vector Line from Vehicle to Selected Camera Pin
      if (selectedCamera != null) {
        val targetX = projectX(selectedCamera.longitude)
        val targetY = projectY(selectedCamera.latitude)
        val activeColor = cameraTypeColor(selectedCamera.type)

        drawLine(
          color = activeColor.copy(alpha = 0.30f),
          start = Offset(vehicleX, vehicleY),
          end = Offset(targetX, targetY),
          strokeWidth = 8f,
          cap = StrokeCap.Round
        )
        drawLine(
          color = activeColor,
          start = Offset(vehicleX, vehicleY),
          end = Offset(targetX, targetY),
          strokeWidth = 3.5f,
          cap = StrokeCap.Round,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        )
      }
    }

    // Road Labels on Map
    Box(
      modifier = Modifier
        .offset(x = 12.dp, y = 26.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color(0xFFFEF3C7).copy(alpha = 0.9f))
        .border(0.5.dp, Color(0xFFF59E0B), RoundedCornerShape(4.dp))
        .padding(horizontal = 5.dp, vertical = 1.dp)
    ) {
      Text("올림픽대로", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
    }

    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .padding(start = 10.dp, top = 36.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color.White.copy(alpha = 0.88f))
        .padding(horizontal = 5.dp, vertical = 1.dp)
    ) {
      Text("강남대로", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }

    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .offset(x = (-32).dp, y = 18.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color.White.copy(alpha = 0.88f))
        .padding(horizontal = 5.dp, vertical = 1.dp)
    ) {
      Text("테헤란로", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }

    // Current Vehicle Location Marker ("내 차량")
    Box(
      modifier = Modifier
        .offset {
          IntOffset(
            x = (vehicleX - 20.dp.toPx()).roundToInt(),
            y = (vehicleY - 20.dp.toPx()).roundToInt()
          )
        }
        .size(40.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size((28 * pulseScale).dp.coerceAtMost(40.dp))
          .clip(CircleShape)
          .background(CyanNeon.copy(alpha = 0.18f))
      )
      Box(
        modifier = Modifier
          .size(22.dp)
          .shadow(3.dp, CircleShape)
          .clip(CircleShape)
          .background(CyanNeon)
          .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Navigation,
          contentDescription = "내 차량 위치",
          tint = Color.White,
          modifier = Modifier.size(13.dp)
        )
      }
    }

    // Pin Markers for Nearby Traffic Enforcement Cameras
    cameras.forEach { camera ->
      val pinX = projectX(camera.longitude)
      val pinY = projectY(camera.latitude)
      val isSelected = selectedCamera?.id == camera.id

      CctvPinMarker(
        camera = camera,
        isSelected = isSelected,
        pinX = pinX,
        pinY = pinY,
        onClick = { onPinClick(camera) }
      )
    }

    // Top-Left Dummy GPS Coordinate HUD Overlay
    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White.copy(alpha = 0.92f))
        .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.MyLocation,
        contentDescription = null,
        tint = CyanNeon,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = String.format("GPS %.4f, %.4f", vehicleCoordinate.latitude, vehicleCoordinate.longitude),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = TextPrimary
      )
    }

    // Top-Right Zoom Toggle Button
    Row(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White.copy(alpha = 0.92f))
        .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
        .clickable { onToggleZoom() }
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .testTag("cctv_map_zoom_toggle"),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.ZoomIn,
        contentDescription = "지도 배율 전환",
        tint = CyanNeon,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = if (isZoomedIn) "반경 500m" else "반경 1.2km",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = CyanNeon
      )
    }

    // Bottom-Left Legend Overlay
    Row(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White.copy(alpha = 0.92f))
        .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      LegendDot(color = AmberWarning, label = "5분 단속")
      LegendDot(color = CrimsonAlert, label = "12만원(보호구역)")
      LegendDot(color = CrimsonAlert, label = "견인 주의")
      LegendDot(color = EmeraldSafe, label = "점심 유예")
    }
  }
}

@Composable
private fun CctvPinMarker(
  camera: EnforcementCamera,
  isSelected: Boolean,
  pinX: Float,
  pinY: Float,
  onClick: () -> Unit
) {
  val pinColor = cameraTypeColor(camera.type)
  val markerWidthDp = 76.dp
  val markerHeightDp = 58.dp

  Column(
    modifier = Modifier
      .offset {
        IntOffset(
          x = (pinX - (markerWidthDp.toPx() / 2f)).roundToInt(),
          y = (pinY - (markerHeightDp.toPx() - 8.dp.toPx())).roundToInt()
        )
      }
      .width(markerWidthDp)
      .clickable { onClick() }
      .testTag("cctv_map_pin_${camera.id}"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Distance & Short Label Callout Pill above the Pin
    Box(
      modifier = Modifier
        .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(8.dp))
        .clip(RoundedCornerShape(8.dp))
        .background(if (isSelected) pinColor else Color.White.copy(alpha = 0.95f))
        .border(
          width = if (isSelected) 1.5.dp else 1.dp,
          color = pinColor,
          shape = RoundedCornerShape(8.dp)
        )
        .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
      Text(
        text = "${camera.distanceMeters}m",
        fontSize = 9.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        color = if (isSelected) Color.White else pinColor,
        maxLines = 1
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    // Pin Head Circle (Speed Limit Sign or Enforcement Badge)
    Box(
      modifier = Modifier
        .size(if (isSelected) 32.dp else 26.dp)
        .shadow(if (isSelected) 5.dp else 2.dp, CircleShape)
        .clip(CircleShape)
        .background(if (camera.speedLimit > 0) Color.White else pinColor)
        .border(
          width = if (isSelected) 3.5.dp else 2.5.dp,
          color = pinColor,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (camera.speedLimit > 0) {
        Text(
          text = "${camera.speedLimit}",
          fontSize = if (isSelected) 12.sp else 10.sp,
          fontWeight = FontWeight.Black,
          color = TextPrimary
        )
      } else {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = camera.title,
          tint = Color.White,
          modifier = Modifier.size(if (isSelected) 15.dp else 12.dp)
        )
      }
    }

    // Pin Pointer Triangle + Ground Shadow Dot
    Canvas(modifier = Modifier.size(width = 12.dp, height = 8.dp)) {
      val path = Path().apply {
        moveTo(size.width * 0.2f, 0f)
        lineTo(size.width * 0.8f, 0f)
        lineTo(size.width * 0.5f, size.height * 0.85f)
        close()
      }
      drawPath(path = path, color = pinColor)
      drawCircle(
        color = pinColor.copy(alpha = 0.35f),
        radius = 3.5f,
        center = Offset(size.width * 0.5f, size.height)
      )
    }
  }
}

@Composable
private fun SelectedCameraDetailPanel(
  camera: EnforcementCamera,
  isStationary: Boolean,
  onSpeakAlert: () -> Unit
) {
  val accentColor = cameraTypeColor(camera.type)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (isStationary) Color(0xFFFFFBEB) else LightSurfaceSubtle)
      .border(
        width = 1.dp,
        color = if (isStationary) AmberWarning.copy(alpha = 0.5f) else accentColor.copy(alpha = 0.35f),
        shape = RoundedCornerShape(14.dp)
      )
      .padding(12.dp)
      .testTag("cctv_map_selected_detail")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Speed limit or Camera badge
          if (camera.speedLimit > 0) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, CrimsonAlert, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${camera.speedLimit}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
              )
            }
          } else {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.5.dp, accentColor, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = camera.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${camera.roadName} · ${camera.type.label} · 전방 ${camera.distanceMeters}m",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = accentColor
            )
            Text(
              text = "좌표: ${camera.coordinateText}",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // TTS Voice Guidance Trigger Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CyanGlow)
            .border(1.dp, CyanNeon.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { onSpeakAlert() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("cctv_map_speak_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "단속 정보 음성 안내",
              tint = CyanNeon,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "음성안내",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CyanNeon
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Operating Hours & Fine Notice Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color.White)
          .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = camera.activeNotice,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStationary) AmberWarning else EmeraldSafe
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = TextSecondary,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = camera.fineNotice,
              fontSize = 10.sp,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MapFilterChip(
  label: String,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) color else LightSurfaceSubtle)
      .border(
        width = 1.dp,
        color = if (isSelected) color else LightCardBorder,
        shape = RoundedCornerShape(10.dp)
      )
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag(tag)
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (isSelected) Color.White else TextSecondary
    )
  }
}

@Composable
private fun LegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextSecondary
    )
  }
}

fun cameraTypeColor(type: CameraType): Color = when (type) {
  CameraType.PARKING_ENFORCEMENT -> AmberWarning
  CameraType.CHILD_ZONE_PARKING -> CrimsonAlert
  CameraType.IMMEDIATE_TOW -> CrimsonAlert
  CameraType.FIRE_HYDRANT -> AmberWarning
  CameraType.LUNCH_GRACE -> EmeraldSafe
  CameraType.SPEED_SIGNAL -> CrimsonAlert
  CameraType.BUS_ONLY_LANE -> CyanNeon
  CameraType.SECTION_CONTROL -> PurpleAccent
}
