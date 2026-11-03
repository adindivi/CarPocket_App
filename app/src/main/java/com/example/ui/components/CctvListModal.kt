package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraType
import com.example.model.EnforcementCamera
import com.example.model.VehicleGpsCoordinate
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CctvListModal(
  cameras: List<EnforcementCamera>,
  selectedCamera: EnforcementCamera? = cameras.firstOrNull(),
  vehicleCoordinate: VehicleGpsCoordinate = VehicleGpsCoordinate(),
  isStationary: Boolean = true,
  onSelectCamera: (EnforcementCamera) -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedFilter by remember { mutableStateOf<CameraType?>(null) }

  val filteredCameras = if (selectedFilter == null) {
    cameras
  } else {
    cameras.filter { it.type == selectedFilter }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = LightSurface,
    scrimColor = Color.Black.copy(alpha = 0.52f),
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = null,
    modifier = Modifier.testTag("cctv_modal_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 12.dp)
        .heightIn(max = 720.dp)
    ) {
      // Sleek Toss/Apple Standard Grab Handle
      Box(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .width(36.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(LightCardBorder)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(CyanGlow)
              .border(1.dp, CyanNeon.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.CameraAlt,
              contentDescription = null,
              tint = CyanNeon,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              "주변 단속 CCTV 지도 & 상세 정보",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              "경찰청·지자체 공공데이터 좌표 연동 (${cameras.size}개 지점)",
              fontSize = 11.sp,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = NoFontPaddingTextStyle
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(Icons.Default.Close, contentDescription = "닫기", tint = TextMuted, modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          CctvFilterChip(
            label = "전체 (${cameras.size})",
            isSelected = selectedFilter == null,
            onClick = { selectedFilter = null }
          )
        }
        items(CameraType.entries.toTypedArray()) { type ->
          CctvFilterChip(
            label = type.label,
            isSelected = selectedFilter == type,
            onClick = { selectedFilter = type }
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Scrollable Map + Camera List
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f, fill = false)
      ) {
        item {
          CctvEnforcementMapView(
            cameras = filteredCameras,
            selectedCamera = selectedCamera,
            vehicleCoordinate = vehicleCoordinate,
            isStationary = isStationary,
            onSelectCamera = onSelectCamera,
            onOpenFullList = null
          )
        }

        items(filteredCameras) { cam ->
          CctvDetailItem(
            camera = cam,
            isSelected = selectedCamera?.id == cam.id,
            onClick = { onSelectCamera(cam) }
          )
        }
      }
    }
  }
}

@Composable
fun CctvFilterChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) CyanNeon else LightSurfaceSubtle)
      .border(1.dp, if (isSelected) CyanNeon else LightCardBorder, RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) Color.White else TextSecondary,
      softWrap = false,
      maxLines = 1,
      style = TextStyle(
        platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
      )
    )
  }
}

@Composable
fun CctvDetailItem(
  camera: EnforcementCamera,
  isSelected: Boolean = false,
  onClick: () -> Unit = {}
) {
  val accentColor = cameraTypeColor(camera.type)
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) CyanGlow.copy(alpha = 0.45f) else LightSurfaceSubtle)
      .border(
        width = 1.dp,
        color = if (isSelected) accentColor else LightCardBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable { onClick() }
      .padding(14.dp)
      .testTag("cctv_list_item_${camera.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Text(
            text = camera.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(accentColor.copy(alpha = 0.14f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = camera.type.label,
              fontSize = 10.sp,
              color = accentColor,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
          text = "${camera.distanceMeters}m",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = EmeraldSafe,
          softWrap = false,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "${camera.roadName} · GPS ${camera.coordinateText}",
          fontSize = 11.sp,
          color = TextSecondary,
          fontFamily = FontFamily.Monospace,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = camera.activeNotice,
        fontSize = 11.sp,
        color = AmberWarning,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
          platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
        )
      )

      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Default.Info,
          contentDescription = null,
          tint = TextMuted,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = camera.fineNotice,
          fontSize = 10.sp,
          color = TextMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )
      }
    }
  }
}
