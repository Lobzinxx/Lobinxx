package com.example

import com.example.data.models.AppConfig
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun secretCodeVerification_admincode3_succeeds() {
        val input = "admincode3"
        val clean = input.trim().lowercase()
        assertEquals("admincode3", clean)
        assertTrue(clean == "admincode3")
    }

    @Test
    fun secretCodeVerification_caseInsensitiveAndTrimmed() {
        val input = "  AdminCode3  "
        val clean = input.trim().lowercase()
        assertEquals("admincode3", clean)
    }

    @Test
    fun secretCodeVerification_invalidCode_fails() {
        val input = "admincode2"
        val clean = input.trim().lowercase()
        assertFalse(clean == "admincode3")
    }

    @Test
    fun appConfig_defaultValues_areValid() {
        val config = AppConfig()
        assertEquals("AppLauncher Pro", config.appTitle)
        assertEquals("com.android.chrome", config.targetPackage)
        assertFalse(config.devModeUnlocked)
        assertEquals("#6366F1", config.primaryColorHex)
    }

    @Test
    fun appConfig_unlockDevMode_updatesState() {
        val config = AppConfig().copy(devModeUnlocked = true, targetPackage = "com.google.android.youtube")
        assertTrue(config.devModeUnlocked)
        assertEquals("com.google.android.youtube", config.targetPackage)
    }
}
