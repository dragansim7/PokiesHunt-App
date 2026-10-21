package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val InstrumentSerifFamily = FontFamily(
  Font(R.font.instrument_serif, FontWeight.Normal)
)

val InterFamily = FontFamily(
  Font(R.font.inter, FontWeight.Normal)
)

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = InstrumentSerifFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 44.sp,
    lineHeight = 48.sp
  ),
  displayMedium = TextStyle(
    fontFamily = InstrumentSerifFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 32.sp,
    lineHeight = 36.sp
  ),
  titleLarge = TextStyle(
    fontFamily = InstrumentSerifFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 28.sp
  ),
  titleMedium = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.2.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp
  ),
  bodySmall = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 15.sp
  ),
  labelLarge = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    letterSpacing = 0.5.sp
  ),
  labelSmall = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    letterSpacing = 1.2.sp
  )
)
