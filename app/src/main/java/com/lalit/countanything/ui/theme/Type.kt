package com.lalit.countanything.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Replace the default Typography with our custom expressive set
val Typography = Typography(
    // For the big counter number: e.g., "42"
    displayLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.ExtraBold, // Very bold for impact
        fontSize = 72.sp, // A bit larger for more presence
        lineHeight = 76.sp,
        letterSpacing = (-0.25).sp
    ),
    // For titles like "Cigarettes Smoked Today"
    headlineSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold, // Strong, but not as loud as the number
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // --- ADD THIS STYLE FOR THE TOP APP BAR ---
    titleLarge = TextStyle(
        fontFamily = Poppins, // Or Montserrat, whichever you prefer for titles
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, // Standard M3 size for center-aligned top app bar title
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // For body text, like labels and descriptions
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // For smaller text, like calendar day numbers
    bodySmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // For button text and navigation labels
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    /* Define other text styles as needed */
)
