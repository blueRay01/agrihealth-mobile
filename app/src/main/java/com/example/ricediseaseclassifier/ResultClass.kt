package com.example.ricediseaseclassifier

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    bitmap: Bitmap?,
    result: PredictionResult?,
    onTryAgain: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Back arrow at top-left
        IconButton(
            onClick = onBackToDashboard,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back to Dashboard",
                tint = Color.Gray
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, start = 25.dp, end = 25.dp), // leave space for arrow
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan Result",
                fontFamily = ptSansBold,
                fontSize = 24.sp,
                color = Color(0xFF6E9277)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display the image
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display result
            if (result != null) {
                Text(
                    text = if (result.isUnknown) "Unknown Plant" else result.label.replace("_", " "),
                    fontFamily = ptSansBold,
                    fontSize = 22.sp,
                    color = if (result.isUnknown) Color(0xFFB00020) else Color.Black
                )

                if (!result.isUnknown) {
                    Text(
                        text = "Accuracy: %.2f%%".format(result.confidence),
                        fontFamily = calibriRegular,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onTryAgain,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E9277)),
                modifier = Modifier.width(200.dp)
            ) {
                Text("Try Again", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
