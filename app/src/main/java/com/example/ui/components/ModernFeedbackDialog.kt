package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IssuedCoupon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NoFontPaddingTextStyle
import kotlinx.coroutines.delay

/**
 * Modern Fintech Feedback Dialog (Toss / Apple HIG standard)
 * Features:
 * - 52% soft black backdrop scrim with touch interception
 * - Spring / ease scale-in & fade-in entrance
 * - Monotone Hero Vector Icon inside tinted capsule circle
 * - Deep slate primary headline & soft gray secondary description
 * - Optional customizable content slot (metadata rows, barcode, stats)
 * - Primary Hero CTA + optional Secondary neutral cancel button
 */
@Composable
fun ModernFeedbackDialog(
  icon: ImageVector = Icons.Default.Check,
  iconTint: Color = EmeraldSafe,
  iconBgColor: Color = EmeraldGlow,
  title: String,
  subtitle: String,
  primaryButtonText: String = "확인",
  primaryButtonColor: Color = CyanNeon,
  secondaryButtonText: String? = null,
  onPrimaryClick: () -> Unit,
  onSecondaryClick: (() -> Unit)? = null,
  onDismiss: () -> Unit,
  testTag: String = "modern_feedback_dialog",
  contentSlot: (@Composable () -> Unit)? = null
) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.52f))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onDismiss
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      modifier = Modifier
        .padding(horizontal = 24.dp)
        .fillMaxWidth()
        .widthIn(max = 380.dp)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = { /* Intercept click so dialog inside won't dismiss */ }
        ),
      shape = RoundedCornerShape(24.dp),
      color = LightSurface,
      shadowElevation = 16.dp,
      border = BorderStroke(1.dp, LightCardBorder)
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top right close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "닫기",
              tint = TextMuted,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Hero Monotone Icon Badge
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(iconBgColor)
            .border(1.dp, iconTint.copy(alpha = 0.25f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Headline
        Text(
          text = title,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          textAlign = TextAlign.Center,
          style = NoFontPaddingTextStyle
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle description
        Text(
          text = subtitle,
          fontSize = 13.sp,
          color = TextSecondary,
          lineHeight = 18.sp,
          textAlign = TextAlign.Center,
          style = NoFontPaddingTextStyle
        )

        // Optional custom content slot (e.g. details, metadata, preview)
        if (contentSlot != null) {
          Spacer(modifier = Modifier.height(14.dp))
          contentSlot()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        if (secondaryButtonText != null && onSecondaryClick != null) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onSecondaryClick,
              modifier = Modifier
                .weight(1f)
                .height(44.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = LightSurfaceSubtle,
                contentColor = TextSecondary
              ),
              border = BorderStroke(1.dp, LightCardBorder),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
              Text(
                text = secondaryButtonText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                style = NoFontPaddingTextStyle
              )
            }

            Button(
              onClick = onPrimaryClick,
              modifier = Modifier
                .weight(1.2f)
                .height(44.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = primaryButtonColor,
                contentColor = Color.White
              ),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
              Text(
                text = primaryButtonText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                style = NoFontPaddingTextStyle
              )
            }
          }
        } else {
          Button(
            onClick = onPrimaryClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = primaryButtonColor,
              contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
          ) {
            Text(
              text = primaryButtonText,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              style = NoFontPaddingTextStyle
            )
          }
        }
      }
    }
  }
}

/**
 * Modern Confirmation Dialog for Critical User Actions
 * (e.g. Parts Replacement, Score Reset)
 */
@Composable
fun ModernConfirmDialog(
  title: String,
  subtitle: String,
  confirmButtonText: String = "확인",
  cancelButtonText: String = "취소",
  icon: ImageVector = Icons.Default.Build,
  iconTint: Color = CyanNeon,
  iconBgColor: Color = CyanGlow,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
  detailRows: List<Pair<String, String>> = emptyList()
) {
  ModernFeedbackDialog(
    icon = icon,
    iconTint = iconTint,
    iconBgColor = iconBgColor,
    title = title,
    subtitle = subtitle,
    primaryButtonText = confirmButtonText,
    primaryButtonColor = CyanNeon,
    secondaryButtonText = cancelButtonText,
    onPrimaryClick = onConfirm,
    onSecondaryClick = onDismiss,
    onDismiss = onDismiss,
    testTag = "modern_confirm_dialog",
    contentSlot = if (detailRows.isNotEmpty()) {
      {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightCardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            detailRows.forEach { (label, value) ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = label,
                  fontSize = 12.sp,
                  color = TextMuted,
                  style = NoFontPaddingTextStyle
                )
                Text(
                  text = value,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary,
                  style = NoFontPaddingTextStyle
                )
              }
            }
          }
        }
      }
    } else null
  )
}

/**
 * Modern Floating Pill Toast (Dynamic Island style notification)
 * Renders non-blocking instant feedback at top center.
 */
@Composable
fun ModernToastPill(
  message: String,
  icon: ImageVector = Icons.Default.CheckCircle,
  iconTint: Color = EmeraldSafe,
  onDismiss: () -> Unit,
  durationMs: Long = 2500L,
  modifier: Modifier = Modifier
) {
  LaunchedEffect(message) {
    delay(durationMs)
    onDismiss()
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.TopCenter
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF0F172A), // Slate 900
      shadowElevation = 8.dp,
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = message,
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          softWrap = false,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}

/**
 * Modern Coupon Barcode Modal for In-Store / Gas Station Usage
 */
@Composable
fun ModernCouponBarcodeDialog(
  coupon: IssuedCoupon,
  onUseNow: () -> Unit,
  onDismiss: () -> Unit
) {
  ModernFeedbackDialog(
    icon = Icons.Default.ConfirmationNumber,
    iconTint = CyanNeon,
    iconBgColor = CyanGlow,
    title = coupon.title,
    subtitle = "주유소 또는 매장 결제 시 직원에게 바코드를 제시해 주세요.",
    primaryButtonText = "사용 완료하기",
    secondaryButtonText = "닫기",
    onPrimaryClick = onUseNow,
    onSecondaryClick = onDismiss,
    onDismiss = onDismiss,
    testTag = "modern_barcode_modal"
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LightSurfaceSubtle)
        .border(1.dp, LightCardBorder, RoundedCornerShape(14.dp))
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Barcode Graphic Mockup
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .padding(horizontal = 8.dp)
      ) {
        val totalWidth = size.width
        val lineHeight = size.height
        val barCount = 38
        val spacing = totalWidth / barCount

        for (i in 0 until barCount) {
          val isThick = (i % 3 == 0 || i % 7 == 0)
          val strokeWidth = if (isThick) 4.dp.toPx() else 2.dp.toPx()
          val x = i * spacing + spacing / 2

          drawLine(
            color = Color(0xFF0F172A),
            start = Offset(x, 0f),
            end = Offset(x, lineHeight),
            strokeWidth = strokeWidth
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Monospace Barcode Number
      Text(
        text = coupon.barcodeNumber,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 2.sp,
        color = TextPrimary,
        style = NoFontPaddingTextStyle
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Expiry & Issuer Details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "유효기간",
          fontSize = 11.sp,
          color = TextMuted,
          style = NoFontPaddingTextStyle
        )
        Text(
          text = coupon.expiryDate,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = TextSecondary,
          style = NoFontPaddingTextStyle
        )
      }
    }
  }
}
