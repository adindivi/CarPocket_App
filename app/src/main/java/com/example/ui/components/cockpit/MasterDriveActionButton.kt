package com.example.ui.components.cockpit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanNeon
import com.example.util.NoFontPaddingTextStyle

@Composable
fun MasterDriveActionButton(
  isDriving: Boolean,
  onToggleDrive: () -> Unit
) {
  Button(
    onClick = onToggleDrive,
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .testTag("drive_action_button"),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isDriving) CrimsonAlert else CyanNeon,
      contentColor = Color.White
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
  ) {
    Icon(
      imageVector = if (isDriving) Icons.Default.Stop else Icons.Default.PlayArrow,
      contentDescription = null,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = if (isDriving) "운전 마치기" else "안전운전 코칭 시작",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      style = NoFontPaddingTextStyle
    )
  }
}
