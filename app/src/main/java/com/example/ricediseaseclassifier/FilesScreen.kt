package com.example.ricediseaseclassifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import com.example.ricediseaseclassifier.calibriRegular
import com.example.ricediseaseclassifier.ptSansBold

@Composable
fun FilesScreen(
    context: Context,
    onNavigate: (String) -> Unit,
    initialFolder: String = "RiceDiseaseApp"
) {
    var gridMode by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("Images") }
    var currentFolder by remember { mutableStateOf(initialFolder) }

    val typeOptions = listOf("Images", "Folders")

    // Get folders dynamically from MediaStore
    val folders = remember { mutableStateListOf<String>() }

    LaunchedEffect(currentFolder) {
        val folderSet = mutableSetOf<String>()
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.RELATIVE_PATH),
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?",
            arrayOf("%$initialFolder/%"),
            null
        )
        cursor?.use {
            val colIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            while (it.moveToNext()) {
                val path = it.getString(colIndex)
                val folder = path.substringAfter("$initialFolder/").trimEnd('/')
                if (folder.isNotEmpty()) folderSet.add(folder)
            }
        }
        folders.clear()
        folders.addAll(folderSet.sorted())
    }

    // Load images from MediaStore for current folder
    val images = remember(currentFolder) {
        loadImagesFromFolder(context, if (currentFolder == initialFolder) initialFolder else "$initialFolder/$currentFolder")
    }

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

            // Header
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

            // Top row: dropdowns + add folder + toggle view
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Type dropdown
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { expanded = true }) {
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

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        typeOptions.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = {
                                selectedType = type
                                expanded = false
                            })
                        }
                    }
                }

                // Add new folder button
                if (selectedType == "Folders") {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "New Folder",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                val newFolderName = "Folder_${folders.size + 1}"
                                folders.add(newFolderName)
                            }
                    )
                }

                // Toggle grid/list
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

            // Content section
            if (selectedType == "Images") {
                if (images.isEmpty()) {
                    Text("No images yet.", modifier = Modifier.padding(top = 40.dp), fontSize = 16.sp, color = Color.Gray)
                }

                if (gridMode) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(images.size) { index ->
                            val (bitmap, name) = images[index]
                            ImageWithBottomText(bitmap, name, Modifier.height(150.dp))
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(images.size) { index ->
                            val (bitmap, name) = images[index]
                            ImageWithBottomText(bitmap, name, Modifier.height(150.dp))
                        }
                    }
                }
            } else {
                // Folders
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
                                    .clickable { currentFolder = folders[index] },
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
                                    .clickable { currentFolder = folders[index] },
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

        // Bottom nav
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
            BottomNavItem(iconRes = 0, label = "Settings", useMaterialIcon = true) { onNavigate("settings") }
        }
    }
}

private fun loadImagesFromFolder(context: Context, folderPath: String): List<Pair<Bitmap, String>> {
    val images = mutableListOf<Pair<Bitmap, String>>()
    val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
    val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?" else null
    val selectionArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf("%$folderPath%") else null

    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        "${MediaStore.Images.Media.DATE_ADDED} DESC"
    )
    cursor?.use {
        val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (it.moveToNext()) {
            val id = it.getLong(idCol)
            val name = it.getString(nameCol)
            val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            val bitmap = context.contentResolver.openInputStream(contentUri)?.use { stream -> BitmapFactory.decodeStream(stream) }
            bitmap?.let { bmp -> images.add(bmp to name) }
        }
    }
    return images
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
            Icon(imageVector = Icons.Default.Settings, contentDescription = label, modifier = Modifier.size(if (isCentral) 50.dp else 32.dp))
        } else {
            Image(painter = painterResource(id = iconRes), contentDescription = label, modifier = Modifier.size(if (isCentral) 50.dp else 32.dp))
        }

        if (!isCentral) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}
