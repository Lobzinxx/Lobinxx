package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppConfig
import com.example.data.translations.TranslationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    config: AppConfig,
    translationManager: TranslationManager,
    onNavigateSettings: () -> Unit,
    onNavigateDevStudio: () -> Unit,
    onLanguageChange: (String) -> Unit,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    var langMenuExpanded by remember { mutableStateOf(false) }
    val currentLang by translationManager.currentLang.collectAsState()

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 1
                )
                if (config.devModeUnlocked) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable { onNavigateDevStudio() }
                            .testTag("dev_mode_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Dev Mode Active",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "DEV",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
        },
        actions = {
            // Language selector button
            Box {
                IconButton(
                    onClick = { langMenuExpanded = true },
                    modifier = Modifier.testTag("language_selector_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Alterar Idioma"
                    )
                }
                DropdownMenu(
                    expanded = langMenuExpanded,
                    onDismissRequest = { langMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🇧🇷 Português")
                                if (currentLang == "pt") Text("✓", fontWeight = FontWeight.Bold)
                            }
                        },
                        onClick = {
                            onLanguageChange("pt")
                            langMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🇺🇸 English")
                                if (currentLang == "en") Text("✓", fontWeight = FontWeight.Bold)
                            }
                        },
                        onClick = {
                            onLanguageChange("en")
                            langMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🇪🇸 Español")
                                if (currentLang == "es") Text("✓", fontWeight = FontWeight.Bold)
                            }
                        },
                        onClick = {
                            onLanguageChange("es")
                            langMenuExpanded = false
                        }
                    )
                }
            }

            if (config.devModeUnlocked) {
                IconButton(
                    onClick = onNavigateDevStudio,
                    modifier = Modifier.testTag("dev_studio_topbar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Dev Studio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = onNavigateSettings,
                modifier = Modifier.testTag("settings_topbar_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun InsertCodeDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    translationManager: TranslationManager,
    onVerifyCode: (String) -> Boolean,
    onCodeSuccess: () -> Unit
) {
    if (!isOpen) return

    var codeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Key,
                contentDescription = null,
                tint = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = translationManager.get("enter_code_dialog_title", "Inserir Código"),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (success) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = translationManager.get("code_success"),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Insira o código de acesso para desbloquear o Modo Desenvolvedor e o estúdio de customização dinâmica.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = {
                            codeInput = it
                            errorMessage = null
                        },
                        label = { Text("Código") },
                        placeholder = { Text("admincode3") },
                        singleLine = true,
                        isError = errorMessage != null,
                        supportingText = {
                            if (errorMessage != null) {
                                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_secret_code")
                    )
                }
            }
        },
        confirmButton = {
            if (success) {
                Button(
                    onClick = {
                        onDismiss()
                        onCodeSuccess()
                    },
                    modifier = Modifier.testTag("btn_open_dev_after_code")
                ) {
                    Text(translationManager.get("open_dev_studio", "Abrir Painel Dev"))
                }
            } else {
                Button(
                    onClick = {
                        if (onVerifyCode(codeInput)) {
                            success = true
                            errorMessage = null
                        } else {
                            errorMessage = translationManager.get("code_invalid", "Código incorreto.")
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_secret_code")
                ) {
                    Text(translationManager.get("btn_confirm", "Confirmar"))
                }
            }
        },
        dismissButton = {
            if (!success) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_cancel_secret_code")
                ) {
                    Text(translationManager.get("btn_cancel", "Cancelar"))
                }
            }
        }
    )
}
