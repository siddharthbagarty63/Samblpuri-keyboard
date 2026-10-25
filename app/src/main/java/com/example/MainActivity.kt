package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.PreferencesRepository
import com.example.data.StickerRepository
import com.example.ui.HomeScreen
import com.example.ui.KeyboardPreviewScreen
import com.example.ui.MyStickersScreen
import com.example.ui.SettingsScreen
import com.example.ui.SetupGuideScreen
import com.example.ui.StickerGalleryScreen
import com.example.ui.WelcomeScreen
import com.example.ui.theme.SambalpuriKeyboardTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepository = PreferencesRepository(this)
        val stickerRepository = StickerRepository(this)
        val navTarget = intent?.getStringExtra("NAV_TARGET")

        setContent {
            SambalpuriKeyboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SambalpuriAppNavigation(
                        preferencesRepository = preferencesRepository,
                        stickerRepository = stickerRepository,
                        navTarget = navTarget
                    )
                }
            }
        }
    }
}

@Composable
fun SambalpuriAppNavigation(
    preferencesRepository: PreferencesRepository,
    stickerRepository: StickerRepository,
    navTarget: String? = null
) {
    val navController = rememberNavController()
    val startDestination = if (preferencesRepository.welcomeCompleted) "home" else "welcome"

    LaunchedEffect(navTarget) {
        if (!navTarget.isNullOrBlank()) {
            navController.navigate(navTarget)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("welcome") {
            WelcomeScreen(
                onContinue = {
                    preferencesRepository.welcomeCompleted = true
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToPreview = { navController.navigate("preview") },
                onNavigateToGallery = { navController.navigate("gallery") },
                onNavigateToMyStickers = { navController.navigate("my_stickers") },
                onNavigateToGuide = { navController.navigate("guide") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("preview") {
            KeyboardPreviewScreen(
                repository = stickerRepository,
                preferencesRepository = preferencesRepository,
                onBack = { navController.popBackStack() },
                onNavigateToMyStickers = { navController.navigate("my_stickers") }
            )
        }

        composable("gallery") {
            StickerGalleryScreen(
                repository = stickerRepository,
                onBack = { navController.popBackStack() },
                onNavigateToMyStickers = { navController.navigate("my_stickers") }
            )
        }

        composable("my_stickers") {
            MyStickersScreen(
                repository = stickerRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable("guide") {
            SetupGuideScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                preferencesRepository = preferencesRepository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
