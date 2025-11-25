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

class MainActivity : ComponentActivity() {

    private var lastCapturedBitmap: Bitmap? = null
    private var lastPrediction: PredictionResult? = null
    private var currentScreen by mutableStateOf("home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RiceDiseaseClassifierTheme {
                val context = LocalContext.current

                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(false) }
                var onboardingFinished by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val hasSeenOnboarding = sharedPref.getBoolean("has_seen_onboarding", false)
                    showOnboarding = !hasSeenOnboarding
                }

                // ===== Compose camera & gallery launchers =====
                val cameraPermission = Manifest.permission.CAMERA
                val galleryPermission = if (Build.VERSION.SDK_INT >= 33) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    bitmap?.let {
                        lastCapturedBitmap = it
                        lastPrediction = ImageClassifier(context).classify(it)
                        currentScreen = "result" // directly go to results
                    }
                }

                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
                        lastCapturedBitmap = bitmap
                        lastPrediction = bitmap?.let { bmp -> ImageClassifier(context).classify(bmp) }
                        currentScreen = "result"
                    }
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

                // ===== Crossfade navigation =====
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
                            recentImages = lastCapturedBitmap?.let { listOf(it) } ?: emptyList()
                        )

                        "upload" -> UploadScreen(
                            onNavigate = { destination -> currentScreen = destination },
                            onPickFromGallery = { permissionLauncher.launch(arrayOf(galleryPermission)) }
                        )

                        "files" -> FilesScreen(onNavigate = { destination -> currentScreen = destination })

                        "camera" -> {
                            LaunchedEffect(Unit) {
                                permissionLauncher.launch(arrayOf(cameraPermission))
                            }
                            // Optionally, show a placeholder while waiting
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
