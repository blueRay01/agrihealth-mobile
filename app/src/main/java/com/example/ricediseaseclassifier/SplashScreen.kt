package com.example.ricediseaseclassifier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

val ptSansNarrow = FontFamily(
    Font(R.font.pt_sans_narrow_web_bold, FontWeight.Bold)
)

@Composable
fun SplashScreen(
    onTimeout: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Run animation and move to next screen
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000) // length of splash animation
        onTimeout()
    }

    val textAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen_v2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //App Logo
            Image(
                painter = painterResource(id = R.drawable.agrihealth_logo_splashscreen),
                contentDescription = "App Logo",
                modifier = Modifier
                    .offset(x = (-10).dp)
                    .size(160.dp)
            )

            AnimatedVisibility(visible = startAnimation) {
                Box {
                    //Shadow
                    Text(
                        text = "AGRIHEALTH\nMOBILE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = ptSansNarrow,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.5f),
                            fontSize = 40.sp,
                            lineHeight = 50.sp
                        ),
                        modifier = Modifier
                            .offset(x = (-20).dp, y = 4.dp)
                            .graphicsLayer(alpha = textAlpha)
                    )

                    //App Name
                    Text(
                        text = "AGRIHEALTH\nMOBILE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = ptSansNarrow,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 40.sp,
                            lineHeight = 50.sp
                        ),
                        modifier = Modifier
                            .offset(x = (-20).dp)
                            .graphicsLayer(alpha = textAlpha)
                    )
                }
            }
        }
    }
}


