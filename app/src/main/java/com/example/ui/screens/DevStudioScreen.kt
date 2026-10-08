package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.models.AppConfig
import com.example.data.models.ColorPreset
import com.example.data.models.InstalledAppInfo
import com.example.data.repository.AppConfigRepository
import com.example.data.translations.TranslationManager
import com.example.ui.components.AppTopBar
import com.example.ui.theme.parseHexColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

enum class DevTab {
    TEXTS,
    COLORS,
    IMAGES,
    TARGET_APP,
    COMPILE_APK,
    TRANSLATIONS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevStudioScreen(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository,
    onNavigateBack: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(DevTab.TEXTS) }

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = translationManager.get("dev_studio_title", "Painel do Desenvolvedor"),
                config = config,
                translationManager = translationManager,
                onNavigateSettings = { /* Already in dev */ },
                onNavigateDevStudio = { /* Already here */ },
                onLanguageChange = onLanguageChange,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                DevTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = when (tab) {
                                    DevTab.TEXTS -> translationManager.get("tab_texts", "Textos")
                                    DevTab.COLORS -> translationManager.get("tab_colors", "Cores")
                                    DevTab.IMAGES -> translationManager.get("tab_images", "Imagens")
                                    DevTab.TARGET_APP -> translationManager.get("tab_target_app", "App Alvo")
                                    DevTab.COMPILE_APK -> translationManager.get("tab_compile_apk", "Compilar APK")
                                    DevTab.TRANSLATIONS -> translationManager.get("tab_translations", "Traduções")
                                },
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    DevTab.TEXTS -> Icons.Default.TextFields
                                    DevTab.COLORS -> Icons.Default.Palette
                                    DevTab.IMAGES -> Icons.Default.Image
                                    DevTab.TARGET_APP -> Icons.Default.Android
                                    DevTab.COMPILE_APK -> Icons.Default.Build
                                    DevTab.TRANSLATIONS -> Icons.Default.Translate
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    DevTab.TEXTS -> TextsDevTab(config, translationManager, repository)
                    DevTab.COLORS -> ColorsDevTab(config, translationManager, repository)
                    DevTab.IMAGES -> ImagesDevTab(config, translationManager, repository)
                    DevTab.TARGET_APP -> TargetAppDevTab(config, translationManager, repository)
                    DevTab.COMPILE_APK -> CompileApkDevTab(config, translationManager, repository)
                    DevTab.TRANSLATIONS -> TranslationsDevTab(config, translationManager)
                }
            }
        }
    }
}

@Composable
private fun TextsDevTab(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository
) {
    val context = LocalContext.current
    var title by remember(config) { mutableStateOf(config.appTitle) }
    var subtitle by remember(config) { mutableStateOf(config.appSubtitle) }
    var launchBtnText by remember(config) { mutableStateOf(config.launchButtonText) }
    var notice by remember(config) { mutableStateOf(config.noticeText) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = translationManager.get("edit_texts_header", "Customização de Textos em Tempo Real"),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(translationManager.get("label_app_title", "Título do App")) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_app_title")
            )
        }

        item {
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text(translationManager.get("label_app_subtitle", "Subtítulo")) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_app_subtitle")
            )
        }

        item {
            OutlinedTextField(
                value = launchBtnText,
                onValueChange = { launchBtnText = it },
                label = { Text(translationManager.get("label_launch_button", "Texto do Botão de Abrir")) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_launch_button_text")
            )
        }

        item {
            OutlinedTextField(
                value = notice,
                onValueChange = { notice = it },
                label = { Text(translationManager.get("label_notice", "Mensagem de Aviso")) },
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_notice_text")
            )
        }

        item {
            Button(
                onClick = {
                    val updated = config.copy(
                        appTitle = title,
                        appSubtitle = subtitle,
                        launchButtonText = launchBtnText,
                        noticeText = notice
                    )
                    repository.updateConfig(updated)
                    Toast.makeText(context, "Textos dinâmicos atualizados!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_texts"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(translationManager.get("save_changes", "Salvar Alterações"), fontWeight = FontWeight.Bold)
            }
        }

        // Live Preview Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PRÉ-VISUALIZAÇÃO AO VIVO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Button(onClick = {}, enabled = false) {
                        Text(launchBtnText)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorsDevTab(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository
) {
    val context = LocalContext.current
    var primaryHex by remember(config) { mutableStateOf(config.primaryColorHex) }
    var secondaryHex by remember(config) { mutableStateOf(config.secondaryColorHex) }
    var backgroundHex by remember(config) { mutableStateOf(config.backgroundColorHex) }
    var surfaceHex by remember(config) { mutableStateOf(config.surfaceColorHex) }
    var textHex by remember(config) { mutableStateOf(config.textColorHex) }

    val presets = listOf(
        ColorPreset("Cyber Neon", "#6366F1", "#EC4899", "#0F172A", "#1E293B", "#F8FAFC"),
        ColorPreset("Emerald Matrix", "#10B981", "#06B6D4", "#064E3B", "#065F46", "#ECFDF5"),
        ColorPreset("Sunset Glow", "#F43F5E", "#F59E0B", "#18181B", "#27272A", "#FAFAFA"),
        ColorPreset("Deep Cosmos", "#8B5CF6", "#3B82F6", "#0B0F19", "#111827", "#F3F4F6"),
        ColorPreset("Golden Amber", "#F59E0B", "#D97706", "#1C1917", "#292524", "#FEF3C7")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = translationManager.get("presets_palette", "Paletas Predefinidas"),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(presets) { preset ->
                    Card(
                        modifier = Modifier
                            .clickable {
                                primaryHex = preset.primaryHex
                                secondaryHex = preset.secondaryHex
                                backgroundHex = preset.backgroundHex
                                surfaceHex = preset.surfaceHex
                                textHex = preset.textHex
                                val updated = config.copy(
                                    primaryColorHex = preset.primaryHex,
                                    secondaryColorHex = preset.secondaryHex,
                                    backgroundColorHex = preset.backgroundHex,
                                    surfaceColorHex = preset.surfaceHex,
                                    textColorHex = preset.textHex
                                )
                                repository.updateConfig(updated)
                                Toast.makeText(context, "Paleta '${preset.name}' aplicada!", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("preset_${preset.name.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(parseHexColor(preset.primaryHex, Color.Blue)))
                                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(parseHexColor(preset.secondaryHex, Color.Red)))
                                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(parseHexColor(preset.backgroundHex, Color.Black)))
                            }
                            Text(preset.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Editor de Cores HEX",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            ColorInputField(
                label = "Cor Primária",
                hex = primaryHex,
                onHexChange = { primaryHex = it },
                testTag = "input_primary_color"
            )
        }

        item {
            ColorInputField(
                label = "Cor Secundária (Destaque)",
                hex = secondaryHex,
                onHexChange = { secondaryHex = it },
                testTag = "input_secondary_color"
            )
        }

        item {
            ColorInputField(
                label = "Cor de Fundo (Background)",
                hex = backgroundHex,
                onHexChange = { backgroundHex = it },
                testTag = "input_bg_color"
            )
        }

        item {
            ColorInputField(
                label = "Cor de Superfície (Cards / Menus)",
                hex = surfaceHex,
                onHexChange = { surfaceHex = it },
                testTag = "input_surface_color"
            )
        }

        item {
            ColorInputField(
                label = "Cor do Texto",
                hex = textHex,
                onHexChange = { textHex = it },
                testTag = "input_text_color"
            )
        }

        item {
            Button(
                onClick = {
                    val updated = config.copy(
                        primaryColorHex = primaryHex,
                        secondaryColorHex = secondaryHex,
                        backgroundColorHex = backgroundHex,
                        surfaceColorHex = surfaceHex,
                        textColorHex = textHex
                    )
                    repository.updateConfig(updated)
                    Toast.makeText(context, "Cores aplicadas dinamicamente!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_colors"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Palette, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aplicar Cores à Interface", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ColorInputField(
    label: String,
    hex: String,
    onHexChange: (String) -> Unit,
    testTag: String
) {
    val currentColor = parseHexColor(hex, Color.Gray)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(currentColor)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
        )
        OutlinedTextField(
            value = hex,
            onValueChange = onHexChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .testTag(testTag)
        )
    }
}

@Composable
private fun ImagesDevTab(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository
) {
    val context = LocalContext.current
    var selectedPreset by remember(config) { mutableStateOf(config.imagePreset) }
    var customUrl by remember(config) { mutableStateOf(config.customImageUrl) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = translationManager.get("edit_images_header", "Seletor de Imagem e Banners"),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedPreset = "rocket"
                        repository.updateConfig(config.copy(imagePreset = "rocket"))
                        Toast.makeText(context, "Foguete Cósmico selecionado!", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("preset_image_rocket"),
                shape = RoundedCornerShape(16.dp),
                border = if (selectedPreset == "rocket") CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_rocket),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Foguete Cósmico (Padrão)", fontWeight = FontWeight.Bold)
                        Text("Ícone 3D estilizado com propulsão neon e portal", style = MaterialTheme.typography.bodySmall)
                    }
                    if (selectedPreset == "rocket") {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedPreset = "portal"
                        repository.updateConfig(config.copy(imagePreset = "portal"))
                        Toast.makeText(context, "Portal Gamer selecionado!", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("preset_image_portal"),
                shape = RoundedCornerShape(16.dp),
                border = if (selectedPreset == "portal") CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.preset_portal),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Portal Gamer Arcade", fontWeight = FontWeight.Bold)
                        Text("Emblema futurista com asas de cristal de energia", style = MaterialTheme.typography.bodySmall)
                    }
                    if (selectedPreset == "portal") {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedPreset = "banner"
                        repository.updateConfig(config.copy(imagePreset = "banner"))
                        Toast.makeText(context, "Cidade Cyberpunk selecionada!", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("preset_image_banner"),
                shape = RoundedCornerShape(16.dp),
                border = if (selectedPreset == "banner") CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_banner),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cidade Cyberpunk Neon (Panorâmico)", fontWeight = FontWeight.Bold)
                        Text("Launchpad holográfico em metrópole futurista", style = MaterialTheme.typography.bodySmall)
                    }
                    if (selectedPreset == "banner") {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = customUrl,
                onValueChange = { customUrl = it },
                label = { Text("URL de Imagem Externa (Opcional)") },
                placeholder = { Text("https://example.com/banner.png") },
                modifier = Modifier.fillMaxWidth().testTag("input_custom_image_url")
            )
        }

        item {
            Button(
                onClick = {
                    val updated = config.copy(
                        imagePreset = if (customUrl.isNotBlank()) "custom" else selectedPreset,
                        customImageUrl = customUrl
                    )
                    repository.updateConfig(updated)
                    Toast.makeText(context, "Configurações visuais salvas!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_images"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Imagem Ativa", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TargetAppDevTab(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository
) {
    val context = LocalContext.current
    val installedApps by repository.installedApps.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var manualPackage by remember(config) { mutableStateOf(config.targetPackage) }
    var manualAppName by remember(config) { mutableStateOf(config.targetAppName) }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter {
            it.appName.contains(searchQuery, ignoreCase = true) ||
            it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = translationManager.get("target_app_header", "Selecione o Aplicativo a Executar"),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Manual Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Configuração Manual do Pacote",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedTextField(
                        value = manualAppName,
                        onValueChange = { manualAppName = it },
                        label = { Text("Nome de Exibição do App") },
                        modifier = Modifier.fillMaxWidth().testTag("input_target_app_name")
                    )
                    OutlinedTextField(
                        value = manualPackage,
                        onValueChange = { manualPackage = it },
                        label = { Text("Nome do Pacote (Package Name)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_target_app_package")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                repository.setTargetApp(manualPackage.trim(), manualAppName.trim())
                                Toast.makeText(context, "App alvo configurado para $manualAppName!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("btn_save_target_app")
                        ) {
                            Text("Salvar")
                        }
                        OutlinedButton(
                            onClick = {
                                val res = repository.launchTargetApp()
                                if (res.isFailure) {
                                    Toast.makeText(context, res.exceptionOrNull()?.message ?: "Erro", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("btn_test_target_app")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(translationManager.get("btn_test_launch", "Testar"))
                        }
                    }
                }
            }
        }

        // Search Bar for installed apps
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(translationManager.get("search_apps", "Buscar aplicativos instalados...")) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_search_installed_apps")
            )
        }

        item {
            Text(
                text = "Aplicativos Instalados no Dispositivo (${filteredApps.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(filteredApps) { appInfo ->
            val isCurrent = config.targetPackage == appInfo.packageName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        manualAppName = appInfo.appName
                        manualPackage = appInfo.packageName
                        repository.setTargetApp(appInfo.packageName, appInfo.appName)
                        Toast.makeText(context, "${appInfo.appName} definido como aplicativo alvo!", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("app_item_${appInfo.packageName.replace(".", "_")}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appInfo.appName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isCurrent) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selecionado",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompileApkDevTab(
    config: AppConfig,
    translationManager: TranslationManager,
    repository: AppConfigRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCompiling by remember { mutableStateOf(false) }
    var compileProgress by remember { mutableStateOf(0f) }
    var compileLogs by remember { mutableStateOf<List<String>>(emptyList()) }
    var generatedApkFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.BuildCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Column {
                            Text(
                                text = translationManager.get("compile_apk_header", "Compilador & Atualizador de APK"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Versão Atual: ${config.apkVersionName} (Build ${config.apkVersionCode})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = translationManager.get("compile_apk_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Summary of current changes that will be compiled into the APK
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("• Título Modificado: ${config.appTitle}", style = MaterialTheme.typography.labelSmall)
                        Text("• App Alvo a Executar: ${config.targetAppName} (${config.targetPackage})", style = MaterialTheme.typography.labelSmall)
                        Text("• Cor Primária: ${config.primaryColorHex}", style = MaterialTheme.typography.labelSmall)
                        Text("• Próxima Versão Compilada: 1.0.${config.apkVersionCode + 1}", style = MaterialTheme.typography.labelSmall)
                    }

                    // Main Compile Button
                    Button(
                        onClick = {
                            if (!isCompiling) {
                                isCompiling = true
                                errorMessage = null
                                compileLogs = emptyList()
                                coroutineScope.launch {
                                    val steps = listOf(
                                        "Iniciando Gradle Daemon & compilador APK...",
                                        "Exportando configurações dinâmicas e strings traduzidas...",
                                        "Compilando novos recursos e paleta de cores (${config.primaryColorHex})...",
                                        "Empacotando DEX e manifesto com app alvo: ${config.targetPackage}...",
                                        "Assinando APK com chave de release (debug/upload)...",
                                        "Gerando pacote final compatível para atualização direta!"
                                    )

                                    for (i in steps.indices) {
                                        compileProgress = (i + 1) / steps.size.toFloat()
                                        compileLogs = compileLogs + "[${i + 1}/${steps.size}] ${steps[i]}"
                                        delay(450)
                                    }

                                    val res = repository.compileAndGenerateApk()
                                    if (res.isSuccess) {
                                        generatedApkFile = res.getOrNull()
                                        compileLogs = compileLogs + "✅ Sucesso! Pacote ${generatedApkFile?.name} pronto para ser atualizado sobre o app original!"
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao compilar APK"
                                        compileLogs = compileLogs + "❌ Falha: $errorMessage"
                                    }
                                    isCompiling = false
                                }
                            }
                        },
                        enabled = !isCompiling,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_compile_apk"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isCompiling) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compilando Novo APK...")
                        } else {
                            Icon(Icons.Default.Engineering, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(translationManager.get("btn_compile_apk", "Compilar e Gerar Novo APK"), fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isCompiling) {
                        LinearProgressIndicator(
                            progress = { compileProgress },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        // Compilation Terminal / Output Box
        if (compileLogs.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TERMINAL DE COMPILAÇÃO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = if (isCompiling) "BUILD EM ANDAMENTO" else "BUILD FINALIZADO",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCompiling) Color(0xFFFBBF24) else Color(0xFF34D399)
                            )
                        }

                        compileLogs.forEach { log ->
                            Text(
                                text = log,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = if (log.contains("❌")) Color(0xFFF87171) else if (log.contains("✅")) Color(0xFF4ADE80) else Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }

        // Post-compile actions: Install & Share APK
        if (generatedApkFile != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "APK Compilado com Sucesso!",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = generatedApkFile!!.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Direct update button
                        Button(
                            onClick = {
                                val res = repository.installApk(generatedApkFile!!)
                                if (res.isFailure) {
                                    Toast.makeText(context, "Erro ao iniciar instalador: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_install_apk"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = translationManager.get("btn_install_apk", "Atualizar Diretamente sobre o App Original"),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Share APK button
                        OutlinedButton(
                            onClick = {
                                repository.shareApk(generatedApkFile!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_share_apk"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(translationManager.get("btn_share_apk", "Compartilhar / Baixar APK"))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslationsDevTab(
    config: AppConfig,
    translationManager: TranslationManager
) {
    val context = LocalContext.current
    val currentLang by translationManager.currentLang.collectAsState()
    var targetLang by remember { mutableStateOf(currentLang) }
    var searchKey by remember { mutableStateOf("") }

    // Load active map for target language
    var translationsState by remember(targetLang) {
        mutableStateOf(translationManager.getTranslationsForLanguage(targetLang))
    }

    val filteredKeys = remember(translationsState, searchKey) {
        translationsState.keys.filter { key ->
            if (searchKey.isBlank()) true
            else key.contains(searchKey, ignoreCase = true) ||
                 (translationsState[key]?.contains(searchKey, ignoreCase = true) == true)
        }.sorted()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = translationManager.get("translation_editor_title", "Editor de Traduções Local"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = translationManager.get("translation_editor_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("pt" to "Português", "en" to "English", "es" to "Español").forEach { (code, name) ->
                            FilterChip(
                                selected = targetLang == code,
                                onClick = {
                                    targetLang = code
                                    translationsState = translationManager.getTranslationsForLanguage(code)
                                },
                                label = { Text(name) },
                                modifier = Modifier.testTag("lang_chip_$code")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                translationManager.resetToDefaults(targetLang)
                                translationsState = translationManager.getTranslationsForLanguage(targetLang)
                                Toast.makeText(context, "Padrões de fábrica restaurados para $targetLang!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("btn_reset_translations")
                        ) {
                            Text(translationManager.get("btn_reset_defaults", "Restaurar Padrões"), style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                val json = translationManager.exportTranslationsJson(targetLang)
                                Toast.makeText(context, "Exportado! (${json.length} caracteres)", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("btn_export_translations")
                        ) {
                            Text(translationManager.get("btn_export_json", "Exportar JSON"), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchKey,
                onValueChange = { searchKey = it },
                label = { Text(translationManager.get("search_translation_keys", "Filtrar chaves de tradução...")) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_search_translations")
            )
        }

        item {
            Text(
                text = "Chaves Disponíveis (${filteredKeys.size})",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(filteredKeys) { key ->
            var currentVal by remember(key, targetLang) {
                mutableStateOf(translationsState[key] ?: "")
            }

            Card(
                modifier = Modifier.fillMaxWidth().testTag("card_translation_$key"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = currentVal,
                        onValueChange = {
                            currentVal = it
                            translationsState = translationsState + (key to it)
                            translationManager.saveSingleTranslation(targetLang, key, it)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_trans_$key"),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }
        }
    }
}
