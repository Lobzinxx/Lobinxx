package com.example.data.translations

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.File

class TranslationManager(private val context: Context) {

    private val defaultPt = mapOf(
        "app_title" to "AppLauncher Pro",
        "app_subtitle" to "Portal Inteligente de Aplicativos",
        "launch_button" to "ABRIR APLICATIVO",
        "target_app_card_title" to "Aplicativo Configurado",
        "target_package_label" to "Pacote:",
        "status_ready" to "Pronto para Execução",
        "status_dev_active" to "Modo Dev Ativo",
        "status_dev_locked" to "Modo Dev Bloqueado",
        "quick_apps" to "Aplicativos Rápidos",
        "settings_title" to "Configurações",
        "menu_general" to "Geral",
        "menu_game" to "Game",
        "menu_game_desc" to "Ajustes de jogos, performance e códigos secretos",
        "game_options" to "Opções de Game",
        "game_perf_mode" to "Modo Desempenho Ultra",
        "game_fps_counter" to "Mostrar Contador de FPS",
        "menu_code" to "Code",
        "menu_code_desc" to "Área de códigos de desenvolvedor e bônus",
        "code_screen_title" to "Código Secreto",
        "code_status_label" to "Status do Desenvolvedor",
        "btn_insert_code" to "Insert Code",
        "enter_code_dialog_title" to "Inserir Código",
        "enter_code_hint" to "Digite o código aqui (ex: admincode3)",
        "btn_confirm" to "Confirmar",
        "btn_cancel" to "Cancelar",
        "code_success" to "Código 'admincode3' aceito! Modo Desenvolvedor Desbloqueado! 🚀",
        "code_invalid" to "Código inválido. Tente novamente.",
        "open_dev_studio" to "Abrir Painel Dev",
        "dev_studio_title" to "Painel do Desenvolvedor",
        "tab_texts" to "Textos",
        "tab_colors" to "Cores",
        "tab_images" to "Imagens",
        "tab_target_app" to "App Alvo",
        "tab_compile_apk" to "Compilar APK",
        "tab_translations" to "Traduções",
        "edit_texts_header" to "Customização de Textos em Tempo Real",
        "label_app_title" to "Título do App",
        "label_app_subtitle" to "Subtítulo",
        "label_launch_button" to "Texto do Botão de Abrir",
        "label_notice" to "Mensagem de Aviso",
        "save_changes" to "Salvar Alterações",
        "edit_colors_header" to "Customização de Cores da Interface",
        "presets_palette" to "Paletas Predefinidas",
        "edit_images_header" to "Seletor de Imagem e Banners",
        "target_app_header" to "Selecione o Aplicativo a Executar",
        "search_apps" to "Buscar aplicativos instalados...",
        "custom_package_input" to "Ou digite o nome do pacote manualmente",
        "btn_test_launch" to "Testar Abertura",
        "compile_apk_header" to "Compilador & Atualizador de APK",
        "compile_apk_desc" to "Ao confirmar as mudanças, uma nova versão do APK será gerada com os novos textos, cores e app alvo. Você poderá atualizá-la diretamente sobre o aplicativo original.",
        "btn_compile_apk" to "Compilar e Gerar Novo APK",
        "btn_install_apk" to "Atualizar Diretamente sobre o App Original",
        "btn_share_apk" to "Compartilhar / Baixar APK",
        "translation_editor_title" to "Editor de Traduções Local",
        "translation_editor_desc" to "No Modo Desenvolvedor, você pode editar diretamente os textos de qualquer idioma e salvar localmente.",
        "select_language" to "Idioma Selecionado",
        "btn_save_translation" to "Salvar Tradução Local",
        "btn_reset_defaults" to "Restaurar Padrões de Fábrica",
        "btn_export_json" to "Exportar Arquivo JSON",
        "search_translation_keys" to "Filtrar chaves de tradução...",
        "language_portuguese" to "Português",
        "language_english" to "English",
        "language_spanish" to "Español"
    )

    private val defaultEn = mapOf(
        "app_title" to "AppLauncher Pro",
        "app_subtitle" to "Smart Application Portal",
        "launch_button" to "LAUNCH APP",
        "target_app_card_title" to "Configured Application",
        "target_package_label" to "Package:",
        "status_ready" to "Ready to Launch",
        "status_dev_active" to "Dev Mode Active",
        "status_dev_locked" to "Dev Mode Locked",
        "quick_apps" to "Quick Apps",
        "settings_title" to "Settings",
        "menu_general" to "General",
        "menu_game" to "Game",
        "menu_game_desc" to "Gaming adjustments, performance, and secret codes",
        "game_options" to "Game Options",
        "game_perf_mode" to "Ultra Gaming Performance",
        "game_fps_counter" to "Show FPS Counter",
        "menu_code" to "Code",
        "menu_code_desc" to "Developer codes and secret unlocks area",
        "code_screen_title" to "Secret Code",
        "code_status_label" to "Developer Status",
        "btn_insert_code" to "Insert Code",
        "enter_code_dialog_title" to "Insert Code",
        "enter_code_hint" to "Enter code here (e.g. admincode3)",
        "btn_confirm" to "Confirm",
        "btn_cancel" to "Cancel",
        "code_success" to "Code 'admincode3' accepted! Developer Mode Unlocked! 🚀",
        "code_invalid" to "Invalid code. Please try again.",
        "open_dev_studio" to "Open Dev Studio",
        "dev_studio_title" to "Developer Studio",
        "tab_texts" to "Texts",
        "tab_colors" to "Colors",
        "tab_images" to "Images",
        "tab_target_app" to "Target App",
        "tab_compile_apk" to "Compile APK",
        "tab_translations" to "Translations",
        "edit_texts_header" to "Real-time Text Customization",
        "label_app_title" to "App Title",
        "label_app_subtitle" to "Subtitle",
        "label_launch_button" to "Launch Button Text",
        "label_notice" to "Announcement Text",
        "save_changes" to "Save Changes",
        "edit_colors_header" to "Interface Color Theming",
        "presets_palette" to "Palette Presets",
        "edit_images_header" to "Image & Banner Selector",
        "target_app_header" to "Select Application to Launch",
        "search_apps" to "Search installed apps...",
        "custom_package_input" to "Or type package name manually",
        "btn_test_launch" to "Test Launch",
        "compile_apk_header" to "APK Compiler & Updater",
        "compile_apk_desc" to "Upon confirming changes, a new modified APK will be compiled with your custom texts, colors, and target app. You can update it directly over the original app.",
        "btn_compile_apk" to "Compile & Generate New APK",
        "btn_install_apk" to "Update Directly Over Original App",
        "btn_share_apk" to "Share / Download APK",
        "translation_editor_title" to "Local Translation Editor",
        "translation_editor_desc" to "In Developer Mode, you can edit translation strings for any language and save them locally.",
        "select_language" to "Selected Language",
        "btn_save_translation" to "Save Local Translation",
        "btn_reset_defaults" to "Restore Factory Defaults",
        "btn_export_json" to "Export JSON File",
        "search_translation_keys" to "Search translation keys...",
        "language_portuguese" to "Português",
        "language_english" to "English",
        "language_spanish" to "Español"
    )

    private val defaultEs = mapOf(
        "app_title" to "AppLauncher Pro",
        "app_subtitle" to "Portal Inteligente de Aplicaciones",
        "launch_button" to "ABRIR APLICACIÓN",
        "target_app_card_title" to "Aplicación Configurada",
        "target_package_label" to "Paquete:",
        "status_ready" to "Listo para Iniciar",
        "status_dev_active" to "Modo Dev Activo",
        "status_dev_locked" to "Modo Dev Bloqueado",
        "quick_apps" to "Aplicaciones Rápidas",
        "settings_title" to "Ajustes",
        "menu_general" to "General",
        "menu_game" to "Game",
        "menu_game_desc" to "Ajustes de juegos, rendimiento y códigos secretos",
        "game_options" to "Opciones de Juego",
        "game_perf_mode" to "Rendimiento Ultra Juegos",
        "game_fps_counter" to "Mostrar Contador de FPS",
        "menu_code" to "Code",
        "menu_code_desc" to "Área de códigos de desarrollador y desbloqueos",
        "code_screen_title" to "Código Secreto",
        "code_status_label" to "Estado de Desarrollador",
        "btn_insert_code" to "Insert Code",
        "enter_code_dialog_title" to "Insertar Código",
        "enter_code_hint" to "Introduce el código aquí (ej: admincode3)",
        "btn_confirm" to "Confirmar",
        "btn_cancel" to "Cancelar",
        "code_success" to "¡Código 'admincode3' aceptado! ¡Modo Desarrollador Desbloqueado! 🚀",
        "code_invalid" to "Código inválido. Inténtalo de nuevo.",
        "open_dev_studio" to "Abrir Panel Dev",
        "dev_studio_title" to "Panel de Desarrollador",
        "tab_texts" to "Textos",
        "tab_colors" to "Colores",
        "tab_images" to "Imágenes",
        "tab_target_app" to "App Objetivo",
        "tab_compile_apk" to "Compilar APK",
        "tab_translations" to "Traducciones",
        "edit_texts_header" to "Personalización de Textos en Tiempo Real",
        "label_app_title" to "Título de la App",
        "label_app_subtitle" to "Subtítulo",
        "label_launch_button" to "Texto del Botón de Abrir",
        "label_notice" to "Mensaje de Aviso",
        "save_changes" to "Guardar Cambios",
        "edit_colors_header" to "Personalización de Colores de Interfaz",
        "presets_palette" to "Paletas Predefinidas",
        "edit_images_header" to "Selector de Imagen y Banners",
        "target_app_header" to "Selecciona la Aplicación a Abrir",
        "search_apps" to "Buscar aplicaciones instaladas...",
        "custom_package_input" to "O escribe el nombre del paquete manualmente",
        "btn_test_launch" to "Probar Apertura",
        "compile_apk_header" to "Compilador & Actualizador de APK",
        "compile_apk_desc" to "Al confirmar los cambios, se compilará un nuevo APK con tus textos, colores y app objetivo. Podrás actualizarlo directamente sobre la app original.",
        "btn_compile_apk" to "Compilar y Generar Nuevo APK",
        "btn_install_apk" to "Actualizar Directamente sobre la App Original",
        "btn_share_apk" to "Compartir / Descargar APK",
        "translation_editor_title" to "Editor de Traducciones Local",
        "translation_editor_desc" to "En Modo Desarrollador, puedes editar directamente los textos de cualquier idioma y guardarlos localmente.",
        "select_language" to "Idioma Seleccionado",
        "btn_save_translation" to "Guardar Traducción Local",
        "btn_reset_defaults" to "Restaurar Valores por Defecto",
        "btn_export_json" to "Exportar Archivo JSON",
        "search_translation_keys" to "Filtrar claves de traducción...",
        "language_portuguese" to "Português",
        "language_english" to "English",
        "language_spanish" to "Español"
    )

    private val currentLangFlow = MutableStateFlow("pt")
    val currentLang: StateFlow<String> = currentLangFlow.asStateFlow()

    private val translationsMapFlow = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentTranslations: StateFlow<Map<String, String>> = translationsMapFlow.asStateFlow()

    init {
        loadTranslations(currentLangFlow.value)
    }

    fun setLanguage(lang: String) {
        val normalized = when (lang.lowercase()) {
            "en" -> "en"
            "es" -> "es"
            else -> "pt"
        }
        currentLangFlow.value = normalized
        loadTranslations(normalized)
    }

    fun get(key: String, defaultVal: String = ""): String {
        return translationsMapFlow.value[key]
            ?: getDefaultMap(currentLangFlow.value)[key]
            ?: defaultVal.ifEmpty { key }
    }

    fun getAllCurrentTranslations(): Map<String, String> {
        return translationsMapFlow.value
    }

    fun getTranslationsForLanguage(lang: String): Map<String, String> {
        val defaults = getDefaultMap(lang)
        val overrides = loadOverridesFromFile(lang)
        return defaults + overrides
    }

    fun saveSingleTranslation(lang: String, key: String, value: String) {
        val currentOverrides = loadOverridesFromFile(lang).toMutableMap()
        currentOverrides[key] = value
        saveOverridesToFile(lang, currentOverrides)
        if (lang == currentLangFlow.value) {
            loadTranslations(lang)
        }
    }

    fun resetToDefaults(lang: String) {
        val file = getFileForLang(lang)
        if (file.exists()) {
            file.delete()
        }
        if (lang == currentLangFlow.value) {
            loadTranslations(lang)
        }
    }

    fun exportTranslationsJson(lang: String): String {
        val all = getTranslationsForLanguage(lang)
        val json = JSONObject()
        all.forEach { (k, v) -> json.put(k, v) }
        return json.toString(2)
    }

    fun importTranslationsJson(lang: String, jsonString: String): Boolean {
        return try {
            val json = JSONObject(jsonString)
            val map = mutableMapOf<String, String>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.getString(key)
            }
            saveOverridesToFile(lang, map)
            if (lang == currentLangFlow.value) {
                loadTranslations(lang)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun loadTranslations(lang: String) {
        val defaults = getDefaultMap(lang)
        val overrides = loadOverridesFromFile(lang)
        translationsMapFlow.value = defaults + overrides
    }

    private fun getDefaultMap(lang: String): Map<String, String> {
        return when (lang) {
            "en" -> defaultEn
            "es" -> defaultEs
            else -> defaultPt
        }
    }

    private fun getFileForLang(lang: String): File {
        return File(context.filesDir, "translations_override_$lang.json")
    }

    private fun loadOverridesFromFile(lang: String): Map<String, String> {
        val file = getFileForLang(lang)
        if (!file.exists()) return emptyMap()
        return try {
            val content = file.readText()
            val json = JSONObject(content)
            val map = mutableMapOf<String, String>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.getString(key)
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun saveOverridesToFile(lang: String, map: Map<String, String>) {
        try {
            val file = getFileForLang(lang)
            val json = JSONObject()
            map.forEach { (k, v) -> json.put(k, v) }
            file.writeText(json.toString(2))
        } catch (_: Exception) {
        }
    }
}
