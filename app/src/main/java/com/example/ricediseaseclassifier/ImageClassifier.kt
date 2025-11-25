package com.example.ricediseaseclassifier

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val inputSize = 224 // must match your TFLite model input
    private val labels = listOf(
        "bacterial_leaf_blight",
        "healthy_rice_plant",
        "narrow_brown_spot",
        "ragged_stunt_virus",
        "rice_blast",
        "rice_false_smut",
        "sheath_blight",
        "sheath_rot",
        "stem_rot",
        "tungro_virus"
    )
     
    init {
        loadModel()
    }

    private fun loadModel() {
        val modelFile = context.assets.open("mobilenetv2_rice_model_quantized.tflite").readBytes()
        val buffer = ByteBuffer.allocateDirect(modelFile.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(modelFile)
        buffer.rewind()
        interpreter = Interpreter(buffer)
    }

    fun classify(bitmap: Bitmap): PredictionResult {
        val inputBuffer = preprocessImage(bitmap)
        val outputBuffer = Array(1) { FloatArray(labels.size) }

        interpreter?.run(inputBuffer, outputBuffer)

        val predictions = outputBuffer[0]
        val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: -1

        if (maxIndex == -1) {
            return PredictionResult(
                label = "Unknown",
                confidence = 0f,
                isUnknown = true
            )
        }

        val confidence = predictions[maxIndex] * 100f

        // If model is very unsure (<20%), treat as unknown
        if (confidence < 20f) {
            return PredictionResult(
                label = "Unknown",
                confidence = confidence,
                isUnknown = true
            )
        }

        return PredictionResult(
            label = labels[maxIndex],
            confidence = confidence,
            isUnknown = false
        )
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val buffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        resized.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF).toFloat()
            val g = ((pixel shr 8) and 0xFF).toFloat()
            val b = (pixel and 0xFF).toFloat()
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }

        buffer.rewind()
        return buffer
    }

}
