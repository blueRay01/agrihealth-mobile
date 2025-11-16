package com.example.ricediseaseclassifier

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*

@Composable
fun OnboardingScreens(onFinish: () -> Unit) {
    var currentPage by remember { mutableStateOf(1) }

    Crossfade(targetState = currentPage) { page ->
        when (page) {
            1 -> GettingStartedScreen1(
                onNext = { currentPage = 2 }
            )
            2 -> GettingStartedScreen2(
                onNext = { currentPage = 3 } // move to screen 3
            )
            3 -> GettingStartedScreen3(
                onNext = onFinish // last screen finishes onboarding
            )
        }
    }
}
