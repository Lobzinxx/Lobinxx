package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.AppConfigRepository
import com.example.data.translations.TranslationManager
import com.example.ui.screens.DevStudioScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DynamicAppTheme

enum class AppNavScreen {
    HOME,
    SETTINGS,
    DEV_STUDIO
}

class MainActivity : ComponentActivity() {

    private lateinit var translationManager: TranslationManager
    private lateinit var repository: AppConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        translationManager = TranslationManager(applicationContext)
        repository = AppConfigRepository(applicationContext)

        setContent {
            val config by repository.config.collectAsStateWithLifecycle()
            val currentTranslations by translationManager.currentTranslations.collectAsStateWithLifecycle()
            var currentScreen by remember { mutableStateOf(AppNavScreen.HOME) }

            // Apply dynamic theming based on AppConfig colors
            DynamicAppTheme(config = config) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        AppNavScreen.HOME -> {
                            HomeScreen(
                                config = config,
                                translationManager = translationManager,
                                repository = repository,
                                onNavigateSettings = { currentScreen = AppNavScreen.SETTINGS },
                                onNavigateDevStudio = { currentScreen = AppNavScreen.DEV_STUDIO },
                                onLanguageChange = { lang ->
                                    translationManager.setLanguage(lang)
                                    repository.updateConfig(config.copy(currentLanguage = lang))
                                }
                            )
                        }
                        AppNavScreen.SETTINGS -> {
                            SettingsScreen(
                                config = config,
                                translationManager = translationManager,
                                repository = repository,
                                onNavigateBack = { currentScreen = AppNavScreen.HOME },
                                onNavigateDevStudio = { currentScreen = AppNavScreen.DEV_STUDIO },
                                onLanguageChange = { lang ->
                                    translationManager.setLanguage(lang)
                                    repository.updateConfig(config.copy(currentLanguage = lang))
                                }
                            )
                        }
                        AppNavScreen.DEV_STUDIO -> {
                            DevStudioScreen(
                                config = config,
                                translationManager = translationManager,
                                repository = repository,
                                onNavigateBack = { currentScreen = AppNavScreen.HOME },
                                onLanguageChange = { lang ->
                                    translationManager.setLanguage(lang)
                                    repository.updateConfig(config.copy(currentLanguage = lang))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
