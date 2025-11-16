package com.example.ricediseaseclassifier

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ricediseaseclassifier.ptSansBold
import com.example.ricediseaseclassifier.calibriRegular

@Composable
fun UploadScreen(onNavigate: (String) -> Unit) {

    var gridMode by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Recent") }
    val dropdownOptions = listOf("Name")

    // ⭐ Changed Box → Column for proper scrolling
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(start = 35.dp, end = 35.dp, top = 25.dp, bottom = 25.dp)
    ) {

        // Wrap the entire scrollable content inside a Column with weight(1f)
        Column(modifier = Modifier.weight(1f)) {

            // 🌾 Logo + app name
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
                    color = Color(0xFF6E9277),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📦 Upload box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .drawBehind {
                        val strokeWidthPx = 2.dp.toPx()
                        val gapPx = 10.dp.toPx()
                        val pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(strokeWidthPx, gapPx),
                            0f
                        )

                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                10.dp.toPx(),
                                10.dp.toPx()
                            ),
                            style = Stroke(width = strokeWidthPx, pathEffect = pathEffect)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.upload_img_icon),
                        contentDescription = "Upload Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Choose picture",
                        fontFamily = calibriRegular,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔽 Recent + grid/list toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(
                        text = selectedOption,
                        fontFamily = calibriRegular,
                        fontSize = 20.sp,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(20.dp)
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        dropdownOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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

            // 📷 Images grid/list (inside weight(1f) container)
            if (gridMode) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(12) { index ->
                        ImageWithBottomText(
                            imageRes = R.drawable.sample_image,
                            fileName = "Image_$index.jpg",
                            modifier = Modifier.height(120.dp) // for LazyColumn items
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(20) { index ->
                        ImageWithBottomText(
                            imageRes = R.drawable.sample_image,
                            fileName = "Image_$index.jpg",
                            modifier = Modifier.height(120.dp) // for LazyColumn items
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ Bottom navigation bar (OUTSIDE scrollable area)
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp)
        ) {
            BottomNavItem(R.drawable.icon1, "Home", modifier = Modifier.weight(1f)) { onNavigate("home") }
            BottomNavItem(R.drawable.icon2_active, "Upload", modifier = Modifier.weight(1f)) { onNavigate("upload") }
            BottomNavItem(R.drawable.icon3, "Camera", isCentral = true, modifier = Modifier.weight(1.2f)) { onNavigate("camera") }
            BottomNavItem(R.drawable.icon4, "Files", modifier = Modifier.weight(1f)) { onNavigate("files") }
            BottomNavItem(iconRes = 0, label = "Settings", useMaterialIcon = true, modifier = Modifier.weight(1f)) { onNavigate("settings") }
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



@Preview(showBackground = true)
@Composable
fun UploadScreenPreview() {
    UploadScreen(onNavigate = {})
}
