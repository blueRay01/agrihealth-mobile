package com.example.ricediseaseclassifier

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.lazy.items       // For LazyColumn
import androidx.compose.foundation.lazy.grid.items  // For LazyVerticalGrid
import com.example.ricediseaseclassifier.ptSansBold
import com.example.ricediseaseclassifier.calibriRegular

@Composable
fun UploadScreen(
    onNavigate: (String) -> Unit,
    onPickFromGallery: () -> Unit,
    userImages: SnapshotStateList<Pair<Bitmap, String>>
) {
    var gridMode by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Recent") }
    val dropdownOptions = listOf("Name")
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
                    .clickable { onPickFromGallery() }
                    .drawBehind {
                        val strokeWidthPx = 2.dp.toPx()
                        val gapPx = 10.dp.toPx()
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(strokeWidthPx, gapPx), 0f)
                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx()),
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

            // 🔽 Recent + toggle
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

            // 📷 Images grid/list
            if (userImages.isNotEmpty()) {
                if (gridMode) {
                    // Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(userImages) { imagePair ->
                            val (bitmap, name) = imagePair
                            // Use the same 120.dp size as DashboardScreen
                            ImageWithBottomText(bitmap, name, modifier = Modifier.size(120.dp))
                        }
                    }
                } else {
                    // List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(userImages) { imagePair ->
                            val (bitmap, name) = imagePair
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Use the same 100.dp size as DashboardScreen list
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Image Preview",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = name,
                                    fontFamily = calibriRegular,
                                    fontSize = 12.sp,  // matches DashboardScreen
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                        text = "No images yet. Tap the box above to add.",
                        fontFamily = calibriRegular,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- Bottom navigation ---
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeight)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .align(Alignment.BottomCenter)
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

@Composable
fun ImageWithBottomText(
    bitmap: Bitmap,
    fileName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(3f/4f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = fileName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom overlay with 20% opacity
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
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun UploadScreenPreview() {
//    UploadScreen(onNavigate = {}, onPickFromGallery = {})
//}


//package com.example.ricediseaseclassifier
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.drawBehind
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.PathEffect
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview


//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.ricediseaseclassifier.ptSansBold
//import com.example.ricediseaseclassifier.calibriRegular
//
//@Composable
//fun UploadScreen(
//    onNavigate: (String) -> Unit,
//    onPickFromGallery: () -> Unit
//) {
//    var gridMode by remember { mutableStateOf(true) }
//    var expanded by remember { mutableStateOf(false) }
//    var selectedOption by remember { mutableStateOf("Recent") }
//    val dropdownOptions = listOf("Name")
//    val navHeight = 70.dp
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF2F2F2))
//    ) {
//
//        // Scrollable content
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(start = 35.dp, end = 35.dp, top = 25.dp, bottom = navHeight)
//                .align(Alignment.TopStart)
//        ) {
//            // 🌾 Logo + app name
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.agrihealth_logo_1),
//                    contentDescription = "App Logo",
//                    modifier = Modifier.size(51.dp),
//                    contentScale = ContentScale.Fit
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//                Text(
//                    text = "AGRIHEALTH MOBILE",
//                    fontFamily = ptSansBold,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    color = Color(0xFF6E9277),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 📦 Upload box
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(144.dp)
//                    .clip(RoundedCornerShape(10.dp))
//                    .background(Color.White)
//                    .clickable { onPickFromGallery() }
//                    .drawBehind {
//                        val strokeWidthPx = 2.dp.toPx()
//                        val gapPx = 10.dp.toPx()
//                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(strokeWidthPx, gapPx), 0f)
//                        drawRoundRect(
//                            color = Color.Transparent,
//                            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
//                            size = size,
//                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx()),
//                            style = Stroke(width = strokeWidthPx, pathEffect = pathEffect)
//                        )
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(
//                        painter = painterResource(id = R.drawable.upload_img_icon),
//                        contentDescription = "Upload Icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "Choose picture",
//                        fontFamily = calibriRegular,
//                        fontSize = 14.sp,
//                        color = Color.Black
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 🔽 Recent + toggle
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.clickable { expanded = true }
//                ) {
//                    Text(
//                        text = selectedOption,
//                        fontFamily = calibriRegular,
//                        fontSize = 20.sp,
//                        color = Color(0xFF333333)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Icon(
//                        imageVector = Icons.Filled.ArrowDropDown,
//                        contentDescription = "Dropdown",
//                        tint = Color(0xFF333333),
//                        modifier = Modifier.size(20.dp)
//                    )
//
//                    DropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false }
//                    ) {
//                        dropdownOptions.forEach { option ->
//                            DropdownMenuItem(
//                                text = { Text(option) },
//                                onClick = {
//                                    selectedOption = option
//                                    expanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//                Box(
//                    modifier = Modifier
//                        .size(36.dp)
//                        .clickable { gridMode = !gridMode },
//                    contentAlignment = Alignment.Center
//                ) {
//                    val icon = if (gridMode) R.drawable.icon_grid else R.drawable.icon_list
//                    Image(
//                        painter = painterResource(id = icon),
//                        contentDescription = "Toggle Grid/List",
//                        modifier = Modifier.size(18.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 📷 Images grid/list
//            if (gridMode) {
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(3),
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(12) { index ->
//                        ImageWithBottomText(
//                            imageRes = R.drawable.sample_image,
//                            fileName = "Image_$index.jpg",
//                            modifier = Modifier.height(120.dp)
//                        )
//                    }
//                }
//            } else {
//                LazyColumn(
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(20) { index ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(4.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.sample_image),
//                                contentDescription = "Image Preview",
//                                modifier = Modifier
//                                    .size(100.dp)
//                                    .clip(RoundedCornerShape(12.dp)),
//                                contentScale = ContentScale.Crop
//                            )
//                            Spacer(modifier = Modifier.width(12.dp))
//                            Text(
//                                text = "Image_$index.jpg",
//                                fontFamily = calibriRegular,
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = Color.Black,
//                                maxLines = 1,
//                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        // --- Fixed Bottom Nav ---
//        Row(
//            horizontalArrangement = Arrangement.SpaceAround,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(navHeight)
//                .background(Color.White, shape = RoundedCornerShape(24.dp))
//                .align(Alignment.BottomCenter)
//                .padding(horizontal = 16.dp)
//        ) {
//            BottomNavItem(R.drawable.icon1, "Home", modifier = Modifier.weight(1f)) { onNavigate("home") }
//            BottomNavItem(R.drawable.icon2_active, "Upload", modifier = Modifier.weight(1f)) { onNavigate("upload") }
//            BottomNavItem(R.drawable.icon3, "Camera", isCentral = true, modifier = Modifier.weight(1.2f)) { onNavigate("camera") }
//            BottomNavItem(R.drawable.icon4, "Files", modifier = Modifier.weight(1f)) { onNavigate("files") }
//            BottomNavItem(iconRes = 0, label = "Settings", useMaterialIcon = true, modifier = Modifier.weight(1f)) { onNavigate("settings") }
//        }
//    }
//}
//
//@Composable
//private fun BottomNavItem(
//    iconRes: Int,
//    label: String,
//    isCentral: Boolean = false,
//    useMaterialIcon: Boolean = false,
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = modifier.clickable { onClick() }
//    ) {
//        if (useMaterialIcon) {
//            Icon(
//                imageVector = Icons.Default.Settings,
//                contentDescription = label,
//                modifier = Modifier.size(if (isCentral) 50.dp else 32.dp)
//            )
//        } else {
//            Image(
//                painter = painterResource(id = iconRes),
//                contentDescription = label,
//                modifier = Modifier.size(if (isCentral) 50.dp else 32.dp)
//            )
//        }
//
//        if (!isCentral) {
//            Text(
//                text = label,
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Medium,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun UploadScreenPreview() {
//    UploadScreen(onNavigate = {}, onPickFromGallery = {})
//}
