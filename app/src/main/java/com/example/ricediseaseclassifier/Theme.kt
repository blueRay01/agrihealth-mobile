// Theme.kt
package com.example.ricediseaseclassifier

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import com.example.ricediseaseclassifier.R

// Fonts
val ptSansBold = FontFamily(Font(R.font.pt_sans_narrow_web_bold, FontWeight.Bold))
val calibriRegular = FontFamily(Font(R.font.calibri_regular, FontWeight.Normal))

// Colors (example, add more as needed)
val BackgroundGray = Color(0xFFF2F2F2)
val PrimaryGreen = Color(0xFF6E9277)
val DarkText = Color(0xFF333333)
val DottedLineColor = Color(0xFFABB7C2).copy(alpha = 0.8f)
