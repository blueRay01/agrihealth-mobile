package com.example.ricediseaseclassifier

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import com.example.ricediseaseclassifier.ui.theme.RiceDiseaseClassifierTheme

class MainActivity : ComponentActivity() {

    private var lastCapturedBitmap by mutableStateOf<Bitmap?>(null)
    private var lastPrediction by mutableStateOf<PredictionResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RiceDiseaseClassifierTheme {
                val context = LocalContext.current

                // --- SharedPreferences: check onboarding ---
                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val hasSeenOnboarding = remember { sharedPref.getBoolean("has_seen_onboarding", false) }

                // --- App state ---
                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(!hasSeenOnboarding) }
                var currentScreen by remember { mutableStateOf("home") }

                // --- Captured images ---
                val savedImages = remember { mutableStateListOf<Pair<Bitmap, String>>() }

                // Load images from internal storage
                LaunchedEffect(Unit) {
                    savedImages.clear()
                    savedImages.addAll(loadAllGalleryImages(context))
                }

                // --- Camera launcher ---
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    bitmap?.let {
                        lastCapturedBitmap = it
                        lastPrediction = ImageClassifier(context).classify(it)

                        val uri = saveBitmapToGallery(context, it)
                        uri?.let { _ ->
                            val fileName = "IMG_${System.currentTimeMillis()}.png"
                            savedImages.add(it to fileName)
                        }

                        currentScreen = "result"
                    }
                }

                // --- Gallery launcher ---
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
                        lastCapturedBitmap = bitmap
                        lastPrediction = bitmap?.let { bmp -> ImageClassifier(context).classify(bmp) }

                        bitmap?.let { bmp ->
                            val savedUri = saveBitmapToGallery(context, bitmap)
                            savedUri?.let {
                                // reload all gallery images so Dashboard and Upload are in sync
                                savedImages.clear()
                                savedImages.addAll(loadAllGalleryImages(context))
                            }
                        }

                        currentScreen = "result"
                    }
                }

                // --- Permissions ---
                val cameraPermission = Manifest.permission.CAMERA
                val galleryPermission = if (Build.VERSION.SDK_INT >= 33)
                    Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions[cameraPermission] == true -> cameraLauncher.launch(null)
                        permissions[galleryPermission] == true -> galleryLauncher.launch("image/*")
                        else -> Toast.makeText(context, "Required permission denied", Toast.LENGTH_SHORT).show()
                    }
                }

                // --- App navigation ---
                Crossfade(
                    targetState = when {
                        showSplash -> "splash"
                        showOnboarding -> "onboarding"
                        else -> currentScreen
                    }
                ) { screen ->
                    when (screen) {
                        "splash" -> SplashScreen(onTimeout = { showSplash = false })
                        "onboarding" -> OnboardingScreens(onFinish = {
                            with(sharedPref.edit()) {
                                putBoolean("has_seen_onboarding", true)
                                apply()
                            }
                            showOnboarding = false
                            currentScreen = "home"
                        })
                        "home" -> DashboardScreen(
                            onNavigate = { destination -> currentScreen = destination },
                            recentImages = savedImages
                        )
                        "upload" -> UploadScreen(
                            onNavigate = { destination -> currentScreen = destination },
                            onPickFromGallery = { permissionLauncher.launch(arrayOf(galleryPermission)) },
                            userImages = savedImages
                        )
                        "files" -> FilesScreen(
                            context = context,
                            onNavigate = { destination -> currentScreen = destination }
                        )
                        "camera" -> {
                            LaunchedEffect(Unit) { permissionLauncher.launch(arrayOf(cameraPermission)) }
                            androidx.compose.material3.Text(
                                "Opening camera...",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        "result" -> ResultScreen(
                            bitmap = lastCapturedBitmap,
                            result = lastPrediction,
                            onTryAgain = { currentScreen = "camera" },
                            onBackToDashboard = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }

    // --- Save bitmap to internal storage (optional backup) ---
    private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, fileName: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Save bitmap to Gallery ---
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
        val filename = "IMG_${System.currentTimeMillis()}.png"
        var uri: Uri? = null

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/RiceDiseaseApp")
            }

            val contentResolver = context.contentResolver
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri
    }
    fun loadAllGalleryImages(context: Context, folder: String = "RiceDiseaseApp"): List<Pair<Bitmap, String>> {
        val images = mutableListOf<Pair<Bitmap, String>>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?" else null
        val selectionArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            arrayOf("%$folder%") else null

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol)
                val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                val bitmap = context.contentResolver.openInputStream(contentUri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
                bitmap?.let { images.add(it to name) }
            }
        }

        return images
    }
}
