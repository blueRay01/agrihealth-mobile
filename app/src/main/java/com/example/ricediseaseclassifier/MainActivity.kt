package com.example.ricediseaseclassifier

import android.Manifest
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import com.example.ricediseaseclassifier.ui.theme.RiceDiseaseClassifierTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class MainActivity : ComponentActivity() {

    private var lastCapturedBitmap by mutableStateOf<Bitmap?>(null)
    private var lastPrediction by mutableStateOf<PredictionResult?>(null)

    private val gson = Gson()
    private val PREDICTIONS_FILE = "predictions.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RiceDiseaseClassifierTheme {
                val context = LocalContext.current

                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val hasSeenOnboarding = remember {
                    sharedPref.getBoolean("has_seen_onboarding", false)
                }

                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(!hasSeenOnboarding) }
                var currentScreen by remember { mutableStateOf("home") }

                val savedImages = remember { mutableStateListOf<UserImage>() }

                // --- Load persisted predictions + images from folder ---
                LaunchedEffect(Unit) {
                    savedImages.clear()
                    val predictions = loadPredictions(context)

                    loadAllGalleryImages(context).forEach { (bitmap, fileName) ->
                        val pred = predictions[fileName]
                        savedImages.add(
                            UserImage(
                                bitmap = bitmap,
                                fileName = fileName,
                                result = pred?.label ?: "Unknown",
                                confidence = pred?.confidence ?: 0f
                            )
                        )
                    }
                }

                // --- Camera launcher ---
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    bitmap?.let { bmp ->
                        handleNewImage(context, bmp, savedImages)
                        currentScreen = "result"
                    }
                }

                // --- Gallery launcher ---
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        val bmp = BitmapFactory.decodeStream(
                            context.contentResolver.openInputStream(it)
                        )
                        bmp?.let { bitmap ->
                            handleNewImage(context, bitmap, savedImages)
                            currentScreen = "result"
                        }
                    }
                }

                // --- Permission handling ---
                val cameraPermission = Manifest.permission.CAMERA
                val galleryPermission =
                    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
                    else Manifest.permission.READ_EXTERNAL_STORAGE

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions[cameraPermission] == true -> cameraLauncher.launch(null)
                        permissions[galleryPermission] == true -> galleryLauncher.launch("image/*")
                        else -> Toast.makeText(context, "Required permission denied", Toast.LENGTH_SHORT).show()
                    }
                }

                // --- Navigation ---
                Crossfade(
                    targetState = when {
                        showSplash -> "splash"
                        showOnboarding -> "onboarding"
                        else -> currentScreen
                    }
                ) { screen ->
                    when (screen) {
                        "splash" ->
                            SplashScreen(onTimeout = { showSplash = false })

                        "onboarding" ->
                            OnboardingScreens(onFinish = {
                                sharedPref.edit().putBoolean("has_seen_onboarding", true).apply()
                                showOnboarding = false
                                currentScreen = "home"
                            })

                        "home" ->
                            DashboardScreen(
                                onNavigate = { currentScreen = it },
                                recentImages = savedImages,
                                onUploadRequest = {
                                    // When user taps the upload button on home screen
                                    galleryLauncher.launch("image/*")
                                }
                            )

                        "history" ->
                            HistoryScreen(
                                userImages = savedImages,
                                onNavigate = { currentScreen = it }
                            )

                        "camera" -> {
                            LaunchedEffect(Unit) {
                                permissionLauncher.launch(arrayOf(cameraPermission))
                            }
                            androidx.compose.material3.Text(
                                "Opening camera...",
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        "result" ->
                            ResultScreen(
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

    // -----------------------------
    // Unified logic for new images
    // -----------------------------
    private fun handleNewImage(
        context: Context,
        bitmap: Bitmap,
        savedImages: MutableList<UserImage>
    ) {
        val prediction = ImageClassifier(context).classify(bitmap)

        lastCapturedBitmap = bitmap
        lastPrediction = prediction

        val (uri, fileName) = saveBitmapToGallery(context, bitmap)

        if (uri != null && fileName != null) {
            savedImages.add(
                UserImage(
                    bitmap = bitmap,
                    fileName = fileName,
                    result = prediction?.label ?: "Unknown",
                    confidence = prediction?.confidence ?: 0f
                )
            )
            savePrediction(context, fileName, prediction)
        }
    }

    // -----------------------------
    // Save prediction
    // -----------------------------
    private fun savePrediction(context: Context, fileName: String, prediction: PredictionResult?) {
        if (prediction == null) return

        val file = File(context.filesDir, PREDICTIONS_FILE)

        val type = object : TypeToken<MutableMap<String, PredictionResult>>() {}.type
        val existing: MutableMap<String, PredictionResult> =
            if (file.exists()) gson.fromJson(FileReader(file), type) ?: mutableMapOf()
            else mutableMapOf()

        existing[fileName] = prediction

        FileWriter(file).use { gson.toJson(existing, it) }
    }

    private fun loadPredictions(context: Context): Map<String, PredictionResult> {
        val file = File(context.filesDir, PREDICTIONS_FILE)
        if (!file.exists()) return emptyMap()

        val type = object : TypeToken<Map<String, PredictionResult>>() {}.type
        return FileReader(file).use { gson.fromJson(it, type) ?: emptyMap() }
    }

    // -----------------------------
    // Save bitmap to Pictures/RiceDiseaseApp
    // -----------------------------
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Pair<Uri?, String?> {
        val filename = "IMG_${System.currentTimeMillis()}.png"
        var uri: Uri? = null

        try {
            val values = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/RiceDiseaseApp")
            }

            val resolver = context.contentResolver
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri to filename
    }

    // -----------------------------
    // Load all saved images
    // -----------------------------
    private fun loadAllGalleryImages(
        context: Context,
        folder: String = "RiceDiseaseApp"
    ): List<Pair<Bitmap, String>> {

        val images = mutableListOf<Pair<Bitmap, String>>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val selection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            else null

        val selectionArgs =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                arrayOf("%$folder%")
            else null

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

                val uri = android.content.ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }

                bitmap?.let { images.add(it to name) }
            }
        }

        return images
    }
}
