package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightAppBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SafeCoachViewModel

@Composable
fun GleoAiScreen(
  viewModel: SafeCoachViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.chatMessages.collectAsState()
  var inputQuery by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val presetQuestions = listOf(
    "내 차 어디 있어?",
    "주차 요금 알려줘",
    "이번 주 내 운전 어땠어?",
    "오늘 급제동 몇 번 했어?",
    "보험료 얼마나 아낄 수 있어?",
    "엔진오일 언제 갈아야 해?",
    "주변에 단속 카메라 있어?",
    "차선 변경 깜빡이 규칙 알려줘"
  )

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(LightAppBg),
    contentAlignment = Alignment.TopCenter
  ) {
    val bubbleMaxWidth = (maxWidth * 0.78f).coerceAtMost(420.dp)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 680.dp)
    ) {
      // Top Gleo Header (Text-First Modern Fintech Standard)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LightSurface,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              text = "AI 운전 비서 글레오",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "궁금한 운전 데이터, 언제든 편하게 물어보세요",
              fontSize = 11.sp,
              color = TextMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(LightSurfaceSubtle)
              .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "음성 모드 켜짐",
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
      }

      // Quick Question Suggestion Chips (Neutral Capsule)
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .background(LightSurfaceSubtle)
          .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(presetQuestions) { query ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(LightSurface)
              .border(1.dp, LightCardBorder, RoundedCornerShape(8.dp))
              .clickable { viewModel.sendGleoMessage(query) }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = query,
              fontSize = 11.sp,
              color = TextSecondary,
              fontWeight = FontWeight.Medium,
              softWrap = false,
              maxLines = 1,
              style = TextStyle(
                platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
              )
            )
          }
        }
      }

      HorizontalDivider(color = LightCardBorder, thickness = 1.dp)

      // Chat Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(messages) { message ->
          ChatMessageBubble(
            message = message,
            maxBubbleWidth = bubbleMaxWidth,
            onReplayVoice = {
              viewModel.audioCoach.speak(message.text)
            }
          )
        }
      }

      // Bottom Input Bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LightSurface,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, LightCardBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mic button
          IconButton(
            onClick = {
              viewModel.sendGleoMessage("이번 주 내 운전 어땠어?")
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(CyanGlow)
              .testTag("voice_mic_button")
          ) {
            Icon(Icons.Default.Mic, contentDescription = "음성 입력", tint = CyanNeon, modifier = Modifier.size(20.dp))
          }

          Spacer(modifier = Modifier.width(8.dp))

          OutlinedTextField(
            value = inputQuery,
            onValueChange = { inputQuery = it },
            placeholder = {
              Text(
                text = "글레오에게 물어보세요 (예: 주차 요금 알려줘)",
                fontSize = 12.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                  platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
                )
              )
            },
            modifier = Modifier
              .weight(1f)
              .testTag("gleo_text_field"),
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = LightSurfaceSubtle,
              unfocusedContainerColor = LightSurfaceSubtle,
              focusedBorderColor = CyanNeon,
              unfocusedBorderColor = LightCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (inputQuery.isNotBlank()) {
                viewModel.sendGleoMessage(inputQuery)
                inputQuery = ""
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(CyanNeon)
              .testTag("send_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "전송", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}

@Composable
fun ChatMessageBubble(
  message: ChatMessage,
  maxBubbleWidth: Dp = 280.dp,
  onReplayVoice: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    verticalAlignment = Alignment.Bottom
  ) {
    if (!message.isUser) {
      Box(
        modifier = Modifier
          .padding(bottom = 4.dp, end = 8.dp)
          .size(28.dp)
          .clip(CircleShape)
          .background(LightSurfaceSubtle)
          .border(1.dp, LightCardBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "AI",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = CyanNeon,
          style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
        )
      }
    }

    Surface(
      modifier = Modifier.widthIn(max = maxBubbleWidth),
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (message.isUser) 14.dp else 4.dp,
        bottomEnd = if (message.isUser) 4.dp else 14.dp
      ),
      color = if (message.isUser) CyanNeon else LightSurface,
      shadowElevation = 0.5.dp,
      border = BorderStroke(1.dp, if (message.isUser) CyanNeon else LightCardBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = message.text,
          fontSize = 13.sp,
          color = if (message.isUser) Color.White else TextPrimary,
          lineHeight = 18.sp,
          fontWeight = if (message.isUser) FontWeight.Medium else FontWeight.Normal,
          style = TextStyle(
            platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false)
          )
        )

        if (!message.isUser) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(LightSurfaceSubtle)
              .clickable { onReplayVoice() }
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "다시 듣기", tint = TextSecondary, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "소리로 듣기",
              fontSize = 10.sp,
              color = TextSecondary,
              fontWeight = FontWeight.SemiBold,
              style = TextStyle(platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(includeFontPadding = false))
            )
          }
        }
      }
    }
  }
}
