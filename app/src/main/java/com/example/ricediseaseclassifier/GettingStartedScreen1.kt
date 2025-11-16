package com.example.ricediseaseclassifier

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GettingStartedScreen1(onNext: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 🌿 Background image
        Image(
            painter = painterResource(id = R.drawable.splash_screen_v2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main vertical layout (white box + dots + next button)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // 🧭 White translucent box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(500.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .padding(vertical = 55.dp, horizontal = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 🌾 Logo
                    Image(
                        painter = painterResource(id = R.drawable.agrihealth_logo_1),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    // 🌱 Title
                    Text(
                        text = "Welcome to AgriHealth Mobile!",
                        color = Color(0xFF2C3E2E),
                        fontSize = 24.sp,
                        fontFamily = ptSansNarrow,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // 📱 Description
                    Text(
                        text = "AgriHealth Mobile helps Filipino farmers quickly detect rice leaf diseases using smart image analysis. With just a photo, the app provides instant, offline results to support healthier crops and better harvests.",
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Progress indicator (1 of 3)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // First (active)
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6E9277))
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Second (inactive)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Third (inactive)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color(0xFF6E9277), shape = CircleShape)
                    .clickable { onNext() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GettingStartedScreen1Preview() {
    GettingStartedScreen1()
}
