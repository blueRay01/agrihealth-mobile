package com.example.ricediseaseclassifier

data class PredictionResult(
    val label: String,
    val confidence: Float,
    val isUnknown: Boolean
)