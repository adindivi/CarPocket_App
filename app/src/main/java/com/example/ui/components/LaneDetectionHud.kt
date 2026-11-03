package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraMode
import com.example.model.LaneDetectionState
import com.example.model.LineType
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LaneDetectionHud(
  laneState: LaneDetectionState,
  onToggleCamera: () -> Unit,
  onToggleLaneType: () -> Unit,
  onTriggerDeparture: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isAlert = laneState.laneDepartureWarning

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    color = LightSurface,
    shadowElevation = 2.dp,
    border = BorderStroke(1.dp, LightCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Box(
            modifier = Modifier
              .size(9.dp)
              .clip(CircleShape)
              .background(if (isAlert) CrimsonAlert else EmeraldSafe)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "AI 차선 인식 비전",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CyanGlow)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (laneState.currentCameraMode == CameraMode.FRONT) "전방 FHD" else "후방 광각",
              fontSize = 10.sp,
              color = CyanNeon,
              fontWeight = FontWeight.Bold,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedButton(
          onClick = onToggleCamera,
          modifier = Modifier
            .height(32.dp)
            .testTag("toggle_camera_button"),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = LightSurfaceSubtle,
            contentColor = TextSecondary
          ),
          border = BorderStroke(1.dp, LightCardBorder),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Cameraswitch, contentDescription = "카메라 전환", modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            "전/후방",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Daylight Perspective Road & AI Lane Vision Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(144.dp)
          .clip(RoundedCornerShape(14.dp))
          .border(1.dp, LightCardBorder, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val horizonY = h * 0.32f

          // Daylight Sky Gradient (top 32%)
          drawRect(
            brush = Brush.verticalGradient(
              0f to Color(0xFFBAE6FD),
              1f to Color(0xFFE0F2FE)
            ),
            topLeft = Offset.Zero,
            size = Size(w, horizonY)
          )

          // Daylight Road Asphalt Surface (bottom 68%)
          drawRect(
            brush = Brush.verticalGradient(
              0f to Color(0xFF64748B),
              1f to Color(0xFF334155)
            ),
            topLeft = Offset(0f, horizonY),
            size = Size(w, h - horizonY)
          )

          // Trajectory Safe Corridor Trapezoid
          val corridorPath = Path().apply {
            moveTo(w * 0.44f, horizonY)
            lineTo(w * 0.56f, horizonY)
            lineTo(w * 0.74f, h)
            lineTo(w * 0.26f, h)
            close()
          }

          val corridorColor = if (isAlert) CrimsonAlert else EmeraldSafe
          drawPath(
            path = corridorPath,
            brush = Brush.verticalGradient(
              listOf(
                corridorColor.copy(alpha = 0.12f),
                corridorColor.copy(alpha = 0.38f)
              )
            )
          )

          // Center dashed guidelines
          val centerLineEffect = PathEffect.dashPathEffect(floatArrayOf(22f, 16f), 0f)
          drawLine(
            color = Color(0x99FFFFFF),
            start = Offset(w * 0.5f, horizonY),
            end = Offset(w * 0.5f, h - 18f),
            strokeWidth = 3f,
            pathEffect = centerLineEffect
          )

          // Left Lane (Dashed White or Solid)
          val leftPath = Path().apply {
            moveTo(w * 0.43f, horizonY)
            lineTo(w * 0.22f, h)
          }

          val leftColor = if (isAlert && laneState.departureSide == "LEFT") CrimsonAlert else Color.White
          val isLeftDashed = laneState.leftLineType == LineType.DASHED

          drawPath(
            path = leftPath,
            color = leftColor,
            style = Stroke(
              width = if (isLeftDashed) 4.5f else 7f,
              cap = StrokeCap.Round,
              pathEffect = if (isLeftDashed) PathEffect.dashPathEffect(floatArrayOf(26f, 16f), 0f) else null
            )
          )

          // Right Lane (Solid Yellow or Dashed White)
          val rightPath = Path().apply {
            moveTo(w * 0.57f, horizonY)
            lineTo(w * 0.78f, h)
          }

          val rightColor = if (isAlert && laneState.departureSide == "RIGHT") CrimsonAlert
          else if (laneState.rightLineType == LineType.SOLID) AmberWarning
          else Color.White

          val isRightDashed = laneState.rightLineType == LineType.DASHED

          drawPath(
            path = rightPath,
            color = rightColor,
            style = Stroke(
              width = if (isRightDashed) 4.5f else 7f,
              cap = StrokeCap.Round,
              pathEffect = if (isRightDashed) PathEffect.dashPathEffect(floatArrayOf(26f, 16f), 0f) else null
            )
          )

          // Distance Grid Lines
          drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(w * 0.38f, h * 0.55f),
            end = Offset(w * 0.62f, h * 0.55f),
            strokeWidth = 1.5f
          )
          drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(w * 0.31f, h * 0.78f),
            end = Offset(w * 0.69f, h * 0.78f),
            strokeWidth = 1.5f
          )
        }

        // HUD Lane Departure Warning Overlay
        if (isAlert) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .clip(RoundedCornerShape(8.dp))
              .background(CrimsonAlert)
              .padding(horizontal = 14.dp, vertical = 7.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (laneState.departureSide == "LEFT") "좌측 차로 이탈 경고" else "우측 차로 이탈 경고",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                softWrap = false,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            }
          }
        }

        // Live AI Confidence Tag
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.94f))
            .border(1.dp, LightCardBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "AI 인식률 ${(laneState.confidence * 100).toInt()}%",
            fontSize = 10.sp,
            color = EmeraldSafe,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Responsive 2-column Lane Status Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
          Text(
            text = "왼쪽: ${laneState.leftLineType.label}",
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        val isRightSolid = laneState.rightLineType == LineType.SOLID
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isRightSolid) AmberGlow else EmeraldGlow)
            .border(1.dp, if (isRightSolid) AmberWarning.copy(alpha = 0.35f) else EmeraldSafe.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
          Text(
            text = "오른쪽: ${laneState.rightLineType.label}",
            fontSize = 11.sp,
            color = if (isRightSolid) AmberWarning else EmeraldSafe,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Responsive 2-column Test Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onToggleLaneType,
          modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .testTag("toggle_lane_type_button"),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = LightSurfaceSubtle,
            contentColor = TextSecondary
          ),
          border = BorderStroke(1.dp, LightCardBorder),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            "실선 · 점선 바꾸기",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }

        OutlinedButton(
          onClick = { onTriggerDeparture(if (laneState.departureSide == "LEFT") "RIGHT" else "LEFT") },
          modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .testTag("test_departure_button"),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CrimsonGlow.copy(alpha = 0.45f),
            contentColor = CrimsonAlert
          ),
          border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.3f)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            "차선 이탈 테스트",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            style = TextStyle(
              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
            )
          )
        }
      }
    }
  }
}
