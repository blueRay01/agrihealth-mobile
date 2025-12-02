package com.example.ricediseaseclassifier

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    recentImages: SnapshotStateList<UserImage>,
    onUploadRequest: () -> Unit
) {
    val context = LocalContext.current
    var gridMode by remember { mutableStateOf(true) }
    val navHeight = 70.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 35.dp, end = 35.dp, top = 25.dp, bottom = navHeight)
                .align(Alignment.TopStart)
        ) {
            // 🌾 Logo + App Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agrihealth_logo_1),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(51.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AGRIHEALTH MOBILE",
                    fontFamily = ptSansBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    color = Color(0xFF6E9277),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🌟 Welcome banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.welcome_ms),
                    contentDescription = "Welcome Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 25.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "HELLO, WELCOME!",
                        fontFamily = ptSansBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF6E9277)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Detect rice leaf diseases \ninstantly with just a photo — \nfast, easy, and ready even \nwithout internet.",
                        fontFamily = calibriRegular,
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload button -> now handled by MainActivity
            Button(
                onClick = { onUploadRequest() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Upload Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Images title + grid/list toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Images",
                    fontFamily = calibriRegular,
                    fontSize = 20.sp,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.Normal
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { gridMode = !gridMode },
                    contentAlignment = Alignment.Center
                ) {
                    val icon = if (gridMode) R.drawable.icon_grid else R.drawable.icon_list
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Toggle Grid/List",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Images grid/list
            if (recentImages.isNotEmpty()) {
                if (gridMode) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(recentImages.size) { index ->
                            val image = recentImages[index]
                            ImageWithBottomText(
                                bitmap = image.bitmap,
                                fileName = image.fileName,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(recentImages.size) { index ->
                            val image = recentImages[index]
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    bitmap = image.bitmap.asImageBitmap(),
                                    contentDescription = image.fileName,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = image.fileName,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No images yet.",
                        fontFamily = calibriRegular,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 🟢 Figma-style Floating Bottom Navigation
        // 🟢 Figma-style Floating Bottom Navigation with “illusion” bulging camera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            // Bottom Navigation with bigger camera icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                // Main white pill
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp) // space from screen edges
                        .height(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    BottomNavItem(
                        iconRes = R.drawable.icon1_active,
                        label = "Home",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("home") }

                    Spacer(modifier = Modifier.width(64.dp)) // leave space for camera icon

                    BottomNavItem(
                        iconRes = R.drawable.icon2,
                        label = "History",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("history") }
                }

                // Fixed-position larger camera icon
                Image(
                    painter = painterResource(id = R.drawable.icon3),
                    contentDescription = "Camera",
                    modifier = Modifier
                        .size(80.dp) // bigger than other icons
                        .align(Alignment.Center)
                        .offset(y = (-5).dp) // optional: slightly above the pill
                        .clickable { onNavigate("camera") }
                )
            }
        }



        }
}
@Composable
private fun BottomNavItem(
    iconRes: Int,
    label: String,
    isCentral: Boolean = false,
    useMaterialIcon: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.clickable { onClick() }
    ) {
        if (useMaterialIcon) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = label,
                modifier = Modifier.size(if (isCentral) 50.dp else 32.dp)
            )
        } else {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(if (isCentral) 50.dp else 32.dp)
            )
        }

        if (!isCentral) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ImageWithBottomText(
    bitmap: Bitmap,
    fileName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = fileName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.2f))
        )
        Text(
            text = fileName,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
