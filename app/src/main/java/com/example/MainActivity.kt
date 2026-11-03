package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CctvListModal
import com.example.ui.screens.DriveCockpitScreen
import com.example.ui.screens.GleoAiScreen
import com.example.ui.screens.InsuranceScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SafeCoachViewModel

enum class AppNavTab(val label: String, val icon: ImageVector, val tag: String) {
  DRIVE("운전", Icons.Default.Speed, "tab_drive"),
  REPORT("기록", Icons.Default.Assessment, "tab_report"),
  INSURANCE("보험할인", Icons.Default.VerifiedUser, "tab_insurance"),
  MAINTENANCE("내 차", Icons.Default.Build, "tab_maintenance"),
  GLEO_AI("AI 코치", Icons.Default.AutoAwesome, "tab_gleo")
}

class MainActivity : ComponentActivity() {
  private val viewModel: SafeCoachViewModel by viewModels()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(
        android.graphics.Color.TRANSPARENT,
        android.graphics.Color.TRANSPARENT
      ),
      navigationBarStyle = SystemBarStyle.light(
        android.graphics.Color.WHITE,
        android.graphics.Color.LTGRAY
      )
    )

    val neededPermissions = mutableListOf<String>()
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        neededPermissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
      }
    }
    if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
      neededPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
      neededPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    if (neededPermissions.isNotEmpty()) {
      requestPermissions(neededPermissions.toTypedArray(), 101)
    }

    setContent {
      MyApplicationTheme {
        var currentTab by remember { mutableStateOf(AppNavTab.DRIVE) }
        var showCctvModal by remember { mutableStateOf(false) }
        val cockpitState by viewModel.cockpitState.collectAsState()
        val cctvCameras by viewModel.cctvCameras.collectAsState()

        BackHandler(enabled = currentTab != AppNavTab.DRIVE) {
          currentTab = AppNavTab.DRIVE
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
          val isWideLayout = maxWidth >= 600.dp

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = LightAppBg,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
              Column {
                TopAppBar(
                  title = {
                    Text(
                      text = "카포켓 (CarPocket)",
                      fontSize = 18.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary,
                      maxLines = 1,
                      style = TextStyle(
                        platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                      )
                    )
                  },
                  colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightSurface,
                    titleContentColor = TextPrimary
                  ),
                  actions = {
                    // CCTV Radar Quick Action
                    IconButton(
                      onClick = { showCctvModal = true },
                      modifier = Modifier.testTag("action_cctv_radar")
                    ) {
                      Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "단속 카메라 목록",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                      )
                    }

                    // Voice Audio Toggle Action
                    IconButton(
                      onClick = { viewModel.toggleVoiceMute() },
                      modifier = Modifier.testTag("action_voice_toggle")
                    ) {
                      Icon(
                        imageVector = if (cockpitState.isVoiceMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "음성 안내",
                        tint = if (cockpitState.isVoiceMuted) CrimsonAlert else TextSecondary,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                )
                HorizontalDivider(color = LightCardBorder, thickness = 1.dp)
              }
            },
            bottomBar = {
              if (!isWideLayout) {
                Column {
                  HorizontalDivider(color = LightCardBorder, thickness = 1.dp)
                  NavigationBar(
                    containerColor = LightSurface,
                    tonalElevation = 0.dp
                  ) {
                    AppNavTab.entries.forEach { tab ->
                      val selected = currentTab == tab
                      NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                          Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(20.dp)
                          )
                        },
                        label = {
                          Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            style = TextStyle(
                              platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                            )
                          )
                        },
                        colors = NavigationBarItemDefaults.colors(
                          selectedIconColor = CyanNeon,
                          selectedTextColor = CyanNeon,
                          indicatorColor = CyanGlow,
                          unselectedIconColor = TextMuted,
                          unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(tab.tag)
                      )
                    }
                  }
                }
              }
            }
          ) { innerPadding ->
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              if (isWideLayout) {
                NavigationRail(
                  containerColor = LightSurface,
                  modifier = Modifier.fillMaxHeight()
                ) {
                  AppNavTab.values().forEach { tab ->
                    val selected = currentTab == tab
                    NavigationRailItem(
                      selected = selected,
                      onClick = { currentTab = tab },
                      icon = {
                        Icon(
                          imageVector = tab.icon,
                          contentDescription = tab.label,
                          modifier = Modifier.size(20.dp)
                        )
                      },
                      label = {
                        Text(
                          text = tab.label,
                          fontSize = 10.sp,
                          fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                      },
                      colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = CyanNeon,
                        selectedTextColor = CyanNeon,
                        indicatorColor = CyanGlow,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                      ),
                      modifier = Modifier.testTag(tab.tag)
                    )
                  }
                }
                VerticalDivider(color = LightCardBorder)
              }

              Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                when (currentTab) {
                  AppNavTab.DRIVE -> DriveCockpitScreen(
                    viewModel = viewModel,
                    onOpenCctvList = { showCctvModal = true }
                  )
                  AppNavTab.REPORT -> ReportScreen(viewModel = viewModel)
                  AppNavTab.INSURANCE -> InsuranceScreen(viewModel = viewModel)
                  AppNavTab.MAINTENANCE -> MaintenanceScreen(viewModel = viewModel)
                  AppNavTab.GLEO_AI -> GleoAiScreen(viewModel = viewModel)
                }

                if (showCctvModal) {
                  CctvListModal(
                    cameras = cctvCameras,
                    selectedCamera = cockpitState.nearestCctv,
                    vehicleCoordinate = cockpitState.vehicleCoordinate,
                    isStationary = cockpitState.isStationary,
                    onSelectCamera = { viewModel.selectCctvCamera(it) },
                    onDismiss = { showCctvModal = false }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
