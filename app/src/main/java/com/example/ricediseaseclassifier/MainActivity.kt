package com.example.ricediseaseclassifier

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
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
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {

    // ✅ Keep these as class-level state
    private var lastCapturedBitmap by mutableStateOf<Bitmap?>(null)
    private var lastPrediction by mutableStateOf<PredictionResult?>(null)
    private var currentScreen by mutableStateOf("home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RiceDiseaseClassifierTheme {
                val context = LocalContext.current

                // --- Keep track of captured images ---
                val savedImages = remember { mutableStateListOf<Bitmap>() }

                // --- Load images from internal storage on startup ---
                LaunchedEffect(Unit) {
                    context.filesDir.listFiles()?.forEach { file ->
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        bmp?.let { savedImages.add(it) }
                    }
                }

                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(false) }
                var onboardingFinished by remember { mutableStateOf(false) }
// --- Camera & Gallery Launchers ---
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    bitmap?.let {
                        lastCapturedBitmap = it
                        lastPrediction = ImageClassifier(context).classify(it)

                        saveBitmapToInternalStorage(context, it)?.let { uri ->
                            val savedBmp = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                            savedBmp?.let { bmp -> savedImages.add(bmp) }
                        }

                        currentScreen = "result"
                    }
                }

                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
                        lastCapturedBitmap = bitmap
                        lastPrediction = bitmap?.let { bmp -> ImageClassifier(context).classify(bmp) }

                        bitmap?.let { bmp ->
                            saveBitmapToInternalStorage(context, bmp)?.let { uri ->
                                val savedBmp = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                                savedBmp?.let { savedImages.add(it) }
                            }
                        }

                        currentScreen = "result"
                    }
                }

                // --- Permissions ---
                val cameraPermission = Manifest.permission.CAMERA
                val galleryPermission = if (Build.VERSION.SDK_INT >= 33) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val cameraGranted = permissions[cameraPermission] ?: false
                    val galleryGranted = permissions[galleryPermission] ?: false

                    when {
                        cameraGranted -> cameraLauncher.launch(null)
                        galleryGranted -> galleryLauncher.launch("image/*")
                        else -> Toast.makeText(context, "Required permission denied", Toast.LENGTH_SHORT).show()
                    }
                }



                // --- Navigation using Crossfade ---
                Crossfade(
                    targetState = when {
                        showSplash -> "splash"
                        showOnboarding && !onboardingFinished -> "onboarding"
                        else -> currentScreen
                    }
                ) { target ->
                    when (target) {
                        "splash" -> SplashScreen(onTimeout = { showSplash = false })

                        "onboarding" -> OnboardingScreens(onFinish = {
                            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putBoolean("has_seen_onboarding", true)
                                apply()
                            }
                            onboardingFinished = true
                        })

                        "home" -> DashboardScreen(
                            onNavigate = { destination -> currentScreen = destination },
                            recentImages = savedImages
                        )

                        "upload" -> UploadScreen(
                            onNavigate = { destination -> currentScreen = destination },
                            onPickFromGallery = { permissionLauncher.launch(arrayOf(galleryPermission)) }
                        )

                        "files" -> FilesScreen(onNavigate = { destination -> currentScreen = destination })

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
}

// --- Save bitmap to internal storage ---
fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val filename = "IMG_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, filename)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        Uri.fromFile(file)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
