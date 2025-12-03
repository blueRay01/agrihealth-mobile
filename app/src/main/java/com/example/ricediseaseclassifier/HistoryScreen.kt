package com.example.ricediseaseclassifier

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.style.TextOverflow

data class UserImage(
    val bitmap: Bitmap,
    val fileName: String,
    val result: String,
    val confidence: Float
)

@SuppressLint("DefaultLocale")
fun formatLabel(raw: String): String {
    return raw.split("_")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
}

@SuppressLint("DefaultLocale")
@Composable
fun HistoryScreen(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    userImages: SnapshotStateList<UserImage>
) {
    var sortOrder by remember { mutableStateOf("Recent") }

    // Sort images
    val sortedImages = remember(userImages, sortOrder) {
        when (sortOrder) {
            "Recent" -> userImages.toList()
            "Oldest" -> userImages.toList().reversed()
            else -> userImages.toList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6EA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 35.dp, end = 35.dp, top = 25.dp, bottom = 100.dp)
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

            // Sort dropdown
            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(
                        text = sortOrder,
                        fontFamily = calibriRegular,
                        fontSize = 20.sp,
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.Normal
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Sort Dropdown",
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(20.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Recent", "Oldest").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sortOrder = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Images list
            if (sortedImages.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(470.dp)
                ) {
                    items(sortedImages) { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = item.bitmap.asImageBitmap(),
                                contentDescription = item.fileName,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.fileName,
                                    fontFamily = calibriRegular,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${formatLabel(item.result)} (${String.format("%.2f%%", item.confidence)})",
                                    fontFamily = calibriRegular,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6E9277)
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No history yet.",
                        fontFamily = calibriRegular,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
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
                    // Home
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

                    // History
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

                // Camera
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
private fun BottomNavItem(
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
