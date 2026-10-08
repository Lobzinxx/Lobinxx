package com.example.data.models

import android.graphics.drawable.Drawable

data class AppConfig(
    val appTitle: String = "AppLauncher Pro",
    val appSubtitle: String = "Portal Inteligente de Aplicativos",
    val launchButtonText: String = "ABRIR APLICATIVO",
    val noticeText: String = "Toque no botão principal para iniciar o aplicativo configurado.",
    val targetPackage: String = "com.android.chrome",
    val targetAppName: String = "Google Chrome",
    val devModeUnlocked: Boolean = false,
    val primaryColorHex: String = "#6366F1",
    val secondaryColorHex: String = "#EC4899",
    val backgroundColorHex: String = "#0F172A",
    val surfaceColorHex: String = "#1E293B",
    val textColorHex: String = "#F8FAFC",
    val imagePreset: String = "rocket", // "rocket", "portal", "banner", "custom"
    val customImageUrl: String = "",
    val currentLanguage: String = "pt",
    val apkVersionCode: Int = 1,
    val apkVersionName: String = "1.0.0"
)

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean = false,
    val icon: Drawable? = null
)

data class ColorPreset(
    val name: String,
    val primaryHex: String,
    val secondaryHex: String,
    val backgroundHex: String,
    val surfaceHex: String,
    val textHex: String
)

data class BuildStep(
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isInProgress: Boolean = false
)
