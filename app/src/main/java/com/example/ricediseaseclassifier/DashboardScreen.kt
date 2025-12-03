package com.example.ricediseaseclassifier

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.material.icons.filled.ArrowBack


@Composable
fun DashboardScreen(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    recentImages: SnapshotStateList<UserImage>,
    onUploadRequest: () -> Unit,
//    selectedImage: MutableState<UserImage?>,
    onPreviewClick: (UserImage) -> Unit
) {
    val context = LocalContext.current
    var gridMode by remember { mutableStateOf(true) }
    val navHeight = 70.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6EA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 35.dp, end = 35.dp, top = 25.dp, bottom = navHeight)
                .align(Alignment.TopStart)
        ) {
            // Logo & App Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = (-16).dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agrihealth_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(51.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(4.dp))
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

            // Welcome banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFFEE1))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ellipse),
                    contentDescription = "Ellipse Background",
                    modifier = Modifier
                        .size(145.dp)
                        .offset(x = 156.dp, y = 0.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.rice_pic),
                    contentDescription = "Rice Picture",
                    modifier = Modifier
                        .size(126.dp)
                        .offset(x = 168.dp, y = 17.dp)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .offset(y = 2.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "HELLO, WELCOME!",
                        fontFamily = ptSansBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF6E9277)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Detect rice leaf diseases \ninstantly with just a photo \n— fast, easy, and ready \neven without internet.",
                        fontFamily = calibriRegular,
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Upload button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { onUploadRequest() },
                    modifier = Modifier.width(260.dp)
                ) {
                    Text(text = "Upload Image")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Images (Grid/List Toggle)
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
                    val iconSize = if (gridMode) 17.dp else 20.dp
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Toggle Grid/List",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Images grid/list
            if (recentImages.isNotEmpty()) {
                if (gridMode) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        items(recentImages.size) { index ->
                            val image = recentImages[index]
                            ImageWithBottomText(
                                bitmap = image.bitmap,
                                fileName = image.fileName,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clickable { onPreviewClick(image) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        items(recentImages.size) { index ->
                            val image = recentImages[index]
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPreviewClick(image) }
                            ) {
                                Image(
                                    bitmap = image.bitmap.asImageBitmap(),
                                    contentDescription = image.fileName,
                                    modifier = Modifier
                                        .size(60.dp)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-50).dp),
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

        // Navigation Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp)
                        .height(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    BottomNavItem(
                        iconResActive = R.drawable.icon_home_active,
                        iconResInactive = R.drawable.icon_home,
                        label = "Home",
                        isSelected = currentScreen == "home",
                        modifier = Modifier
                            .weight(1f)
                            .offset(y=2.dp)
                    ) { onNavigate("home") }

                    Spacer(modifier = Modifier.width(64.dp))

                    BottomNavItem(
                        iconResActive = R.drawable.icon_history_active,
                        iconResInactive = R.drawable.icon_history,
                        label = "History",
                        isSelected = currentScreen == "history",
                        modifier = Modifier
                            .weight(1f)
                            .offset(y=2.dp)
                    ) { onNavigate("history") }
                }

                Image(
                    painter = painterResource(id = R.drawable.icon_camera),
                    contentDescription = "Camera",
                    modifier = Modifier
                        .size(86.dp)
                        .align(Alignment.Center)
                        .offset(y = (-5).dp)
                        .clickable { onNavigate("camera") }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    iconResActive: Int,
    iconResInactive: Int,
    label: String,
    isSelected: Boolean = false,
    isCentral: Boolean = false,
    useMaterialIcon: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val iconRes = if (isSelected) iconResActive else iconResInactive

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

@Composable
fun PreviewScreen(
    image: UserImage?,
    onBack: () -> Unit
) {
    if (image == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            bitmap = image.bitmap.asImageBitmap(),
            contentDescription = image.fileName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = image.result,
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Confidence: ${String.format("%.2f", image.confidence)}%",
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    }
}