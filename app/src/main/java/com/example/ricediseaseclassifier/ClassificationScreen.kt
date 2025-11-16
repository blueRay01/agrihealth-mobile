package com.example.ricediseaseclassifier

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ClassificationScreen() {
    val context = LocalContext.current
    val classifier = remember { ImageClassifier(context) }
    var resultText by remember { mutableStateOf("No image selected") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraPermission = Manifest.permission.CAMERA
    val readImagesPermission = if (Build.VERSION.SDK_INT  >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedImage = it
            resultText = classifier.classify(it)
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
            selectedImage = bitmap
            resultText = classifier.classify(bitmap)
        }
    }

    // Unified permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[cameraPermission] ?: false
        val galleryGranted = permissions[readImagesPermission] ?: false

        when {
            cameraGranted -> cameraLauncher.launch(null)
            galleryGranted -> galleryLauncher.launch("image/*")
            else -> Toast.makeText(context, "Required permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        selectedImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(250.dp)
                    .padding(8.dp)
            )
        } ?: Text("No image selected")

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { permissionLauncher.launch(arrayOf(cameraPermission)) }) {
                Text("Take Photo")
            }
            Button(onClick = { permissionLauncher.launch(arrayOf(readImagesPermission)) }) {
                Text("Pick from Gallery")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resultText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}
