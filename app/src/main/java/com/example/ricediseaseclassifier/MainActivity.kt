package com.example.ricediseaseclassifier

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.example.ricediseaseclassifier.ui.theme.RiceDiseaseClassifierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RiceDiseaseClassifierTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(false) }
                var onboardingFinished by remember { mutableStateOf(false) }

                // 🔹 Load onboarding flag from SharedPreferences
                LaunchedEffect(Unit) {
                    val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val hasSeenOnboarding = sharedPref.getBoolean("has_seen_onboarding", false)
                    showOnboarding = !hasSeenOnboarding
                }

                // State for bottom navigation
                var currentScreen by remember { mutableStateOf("home") }

                Crossfade(targetState = when {
                    showSplash -> "splash"
                    showOnboarding && !onboardingFinished -> "onboarding"
                    else -> currentScreen
                }) { target ->
                    when (target) {
                        "splash" -> SplashScreen(
                            onTimeout = { showSplash = false }
                        )

                        "onboarding" -> OnboardingScreens(
                            onFinish = {
                                val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putBoolean("has_seen_onboarding", true)
                                    apply()
                                }
                                onboardingFinished = true
                            }
                        )

                        "home" -> DashboardScreen(
                            onNavigate = { destination -> currentScreen = destination }
                        )
                        "upload" -> UploadScreen(
                            onNavigate = { destination -> currentScreen = destination }
                        )
                        "files" -> FilesScreen(
                            onNavigate = { destination -> currentScreen = destination }
                        )
//                        "settings" -> SettingsScreen(
//                            onNavigate = { destination -> currentScreen = destination }
//                        )
                    }
                }
            }
        }
    }
}
