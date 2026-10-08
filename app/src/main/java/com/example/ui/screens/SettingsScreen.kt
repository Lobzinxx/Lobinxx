package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppConfig
import com.example.data.repository.AppConfigRepository
import com.example.data.translations.TranslationManager
import com.example.ui.components.AppTopBar
import com.example.ui.components.InsertCodeDialog

enum class SettingsSubscreen {
    MAIN,
    GAME,
    CODE
}

@Composable
fun SettingsScreen(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository,
    onNavigateBack: () -> Unit,
    onNavigateDevStudio: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var currentSubscreen by remember { mutableStateOf(SettingsSubscreen.MAIN) }
    var isInsertCodeDialogOpen by remember { mutableStateOf(false) }

    // Intercept hardware/system back button
    BackHandler {
        when (currentSubscreen) {
            SettingsSubscreen.CODE -> currentSubscreen = SettingsSubscreen.GAME
            SettingsSubscreen.GAME -> currentSubscreen = SettingsSubscreen.MAIN
            SettingsSubscreen.MAIN -> onNavigateBack()
        }
    }

    val screenTitle = when (currentSubscreen) {
        SettingsSubscreen.MAIN -> translationManager.get("settings_title", "Configurações")
        SettingsSubscreen.GAME -> "Configurações ➔ Game"
        SettingsSubscreen.CODE -> "Game ➔ Code"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
                config = config,
                translationManager = translationManager,
                onNavigateSettings = { /* Already here */ },
                onNavigateDevStudio = onNavigateDevStudio,
                onLanguageChange = onLanguageChange,
                showBackButton = true,
                onBackClick = {
                    when (currentSubscreen) {
                        SettingsSubscreen.CODE -> currentSubscreen = SettingsSubscreen.GAME
                        SettingsSubscreen.GAME -> currentSubscreen = SettingsSubscreen.MAIN
                        SettingsSubscreen.MAIN -> onNavigateBack()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSubscreen) {
                SettingsSubscreen.MAIN -> {
                    MainSettingsContent(
                        config = config,
                        translationManager = translationManager,
                        onOpenGame = { currentSubscreen = SettingsSubscreen.GAME },
                        onOpenDevStudio = onNavigateDevStudio,
                        onLanguageChange = onLanguageChange
                    )
                }
                SettingsSubscreen.GAME -> {
                    GameSettingsContent(
                        translationManager = translationManager,
                        onOpenCode = { currentSubscreen = SettingsSubscreen.CODE }
                    )
                }
                SettingsSubscreen.CODE -> {
                    CodeSettingsContent(
                        config = config,
                        translationManager = translationManager,
                        onOpenInsertCodeDialog = { isInsertCodeDialogOpen = true },
                        onOpenDevStudio = onNavigateDevStudio
                    )
                }
            }
        }
    }

    InsertCodeDialog(
        isOpen = isInsertCodeDialogOpen,
        onDismiss = { isInsertCodeDialogOpen = false },
        translationManager = translationManager,
        onVerifyCode = { code ->
            val success = repository.verifySecretCode(code)
            if (success) {
                Toast.makeText(context, translationManager.get("code_success"), Toast.LENGTH_LONG).show()
            }
            success
        },
        onCodeSuccess = {
            onNavigateDevStudio()
        }
    )
}

@Composable
private fun MainSettingsContent(
    config: AppConfig,
    translationManager: TranslationManager,
    onOpenGame: () -> Unit,
    onOpenDevStudio: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val currentLang by translationManager.currentLang.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Path breadcrumb explanation
        item {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Route,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Caminho do Código Secreto:\nConfigurações ➔ Game ➔ Code ➔ Insert Code",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Section: Game (requested target)
        item {
            SettingsCategoryHeader("Seção Principal")
        }

        item {
            SettingsItemCard(
                title = translationManager.get("menu_game", "Game"),
                subtitle = translationManager.get("menu_game_desc", "Ajustes de jogos, performance e códigos secretos"),
                icon = Icons.Default.SportsEsports,
                testTag = "settings_menu_game",
                onClick = onOpenGame
            )
        }

        // Dev Mode direct entry if unlocked
        if (config.devModeUnlocked) {
            item {
                SettingsCategoryHeader("Modo Desenvolvedor Ativo")
            }
            item {
                SettingsItemCard(
                    title = translationManager.get("dev_studio_title", "Painel do Desenvolvedor"),
                    subtitle = "Customizar textos, cores, imagens, traduções e compilar novo APK",
                    icon = Icons.Default.Tune,
                    highlight = true,
                    testTag = "settings_menu_dev_studio",
                    onClick = onOpenDevStudio
                )
            }
        }

        // Section: General
        item {
            SettingsCategoryHeader(translationManager.get("menu_general", "Geral"))
        }

        item {
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = translationManager.get("select_language", "Idioma"),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = when (currentLang) {
                                        "pt" -> "Português (Brasil)"
                                        "en" -> "English"
                                        "es" -> "Español"
                                        else -> currentLang
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Alterar")
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("🇧🇷 Português") },
                            onClick = {
                                onLanguageChange("pt")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇺🇸 English") },
                            onClick = {
                                onLanguageChange("en")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇪🇸 Español") },
                            onClick = {
                                onLanguageChange("es")
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // App Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "AppLauncher Pro v${config.apkVersionName} (Build ${config.apkVersionCode})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Desenvolvido com Jetpack Compose & Dynamic Theming Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GameSettingsContent(
    translationManager: TranslationManager,
    onOpenCode: () -> Unit
) {
    var perfMode by remember { mutableStateOf(true) }
    var fpsOverlay by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Menu Game ➔ Selecione 'Code' para acessar a área de código secreto.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        item {
            SettingsCategoryHeader("Acesso a Códigos")
        }

        // Step 2: Game -> Code
        item {
            SettingsItemCard(
                title = translationManager.get("menu_code", "Code"),
                subtitle = translationManager.get("menu_code_desc", "Área de códigos de desenvolvedor e bônus"),
                icon = Icons.Default.Key,
                highlight = true,
                testTag = "game_menu_code",
                onClick = onOpenCode
            )
        }

        item {
            SettingsCategoryHeader(translationManager.get("game_options", "Opções de Game"))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = translationManager.get("game_perf_mode", "Modo Desempenho Ultra"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Priorizar taxa de quadros e baixa latência de toque",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = perfMode,
                            onCheckedChange = { perfMode = it }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = translationManager.get("game_fps_counter", "Contador de FPS"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Exibir taxa de quadros (60 / 120 Hz) em tempo real",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = fpsOverlay,
                            onCheckedChange = { fpsOverlay = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeSettingsContent(
    config: AppConfig,
    translationManager: TranslationManager,
    onOpenInsertCodeDialog: () -> Unit,
    onOpenDevStudio: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (config.devModeUnlocked)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (config.devModeUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (config.devModeUnlocked)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Text(
                        text = translationManager.get("code_screen_title", "Área de Códigos"),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Surface(
                        color = if (config.devModeUnlocked)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (config.devModeUnlocked)
                                "⚡ " + translationManager.get("status_dev_active", "Modo Dev Ativo")
                            else
                                "🔒 " + translationManager.get("status_dev_locked", "Modo Dev Bloqueado"),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (config.devModeUnlocked)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = "Toque no botão 'Insert Code' abaixo para inserir o código 'admincode3' e desbloquear o Modo Desenvolvedor dinâmico.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Step 3: Game -> Code -> Insert Code Button
                    Button(
                        onClick = onOpenInsertCodeDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_insert_code"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pin,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = translationManager.get("btn_insert_code", "Insert Code"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (config.devModeUnlocked) {
                        FilledTonalButton(
                            onClick = onOpenDevStudio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_go_to_dev_studio"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = translationManager.get("open_dev_studio", "Abrir Painel Dev"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun SettingsItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (highlight)
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.secondary))
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (highlight)
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (highlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
