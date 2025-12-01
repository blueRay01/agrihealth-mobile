package com.example.ricediseaseclassifier

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.ricediseaseclassifier.ptSansBold
import com.example.ricediseaseclassifier.calibriRegular

@Composable
fun FilesScreen(
    onNavigate: (String) -> Unit,
    savedImages: List<Pair<Bitmap, String>>      // <-- now receives real images
) {
    var gridMode by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("Images") }

    val typeOptions = listOf("Images", "Folders")
    val folders = remember { mutableStateListOf("Rice Diseases", "Fertilizer Tips", "Planting Calendar") }

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
        ) {

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
                        text = selectedType,
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
                        typeOptions.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedType == "Folders") {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "New Folder",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { folders.add("New Folder ${folders.size + 1}") }
                    )
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

            // ----------------------------------------------------------------------
            //                          FILES SECTION
            // ----------------------------------------------------------------------
            if (selectedType == "Images") {

                if (savedImages.isEmpty()) {
                    Text(
                        text = "No images yet.",
                        modifier = Modifier.padding(top = 40.dp),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                // 🔶 GRID MODE
                if (gridMode) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(savedImages.size) { index ->
                            val (bitmap, name) = savedImages[index]
                            ImageWithBottomText(
                                bitmap = bitmap,
                                fileName = name,
                                modifier = Modifier.height(150.dp)   // or any height you want
                            )
                        }
                    }

                    // 🔶 LIST MODE
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(savedImages.size) { index ->
                            val (bitmap, name) = savedImages[index]
                            ImageWithBottomText(
                                bitmap = bitmap,
                                fileName = name,
                                modifier = Modifier.height(150.dp)
                            )
                        }
                    }
                }
            }

            // ----------------------------------------------------------------------
            //                         FOLDER SECTION
            // ----------------------------------------------------------------------
            else {
                if (gridMode) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(folders.size) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFBCE0A9))
                                    .clickable { onNavigate("folder:${folders[index]}") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = folders[index],
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(folders.size) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFBCE0A9))
                                    .clickable { onNavigate("folder:${folders[index]}") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = folders[index],
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // ----------------------------------------------------------------------
        //                           BOTTOM NAV
        // ----------------------------------------------------------------------
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeight)
                .background(Color.White, RoundedCornerShape(24.dp))
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
        ) {
            BottomNavItem(R.drawable.icon1, "Home") { onNavigate("home") }
            BottomNavItem(R.drawable.icon2, "Upload") { onNavigate("upload") }
            BottomNavItem(R.drawable.icon3, "Camera", isCentral = true) { onNavigate("camera") }
            BottomNavItem(R.drawable.icon4_active, "Files") { onNavigate("files") }
            BottomNavItem(iconRes = 0, label = "Settings", useMaterialIcon = true) {
                onNavigate("settings")
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


//@Preview(showBackground = true)
//@Composable
//fun FilesScreenPreview() {
//    FilesScreen(onNavigate = {})
//}
