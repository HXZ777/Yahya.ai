package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AssistantSettings
import com.example.domain.model.OperatingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "hxz_ai_settings",
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val ASSISTANT_NAME = stringPreferencesKey("assistant_name")
        val WAKE_PHRASE = stringPreferencesKey("wake_phrase")
        val SPEECH_LANGUAGE = stringPreferencesKey("speech_language")
        val OPERATING_MODE = stringPreferencesKey("operating_mode")
        val GEMINI_MODEL_ID = stringPreferencesKey("gemini_model_id")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val TTS_VOICE_NAME = stringPreferencesKey("tts_voice_name")
        val WEB_SEARCH_ENABLED = booleanPreferencesKey("web_search_enabled")
        val FOREGROUND_SERVICE_ENABLED = booleanPreferencesKey("foreground_service_enabled")
        val AUTO_SPEAK_REPLIES = booleanPreferencesKey("auto_speak_replies")
        val CONFIRM_SENSITIVE_ACTIONS = booleanPreferencesKey("confirm_sensitive_actions")
        val RTL_ENABLED = booleanPreferencesKey("rtl_enabled")
        val SYSTEM_OVERLAY_ENABLED = booleanPreferencesKey("system_overlay_enabled")
    }

    val settingsFlow: Flow<AssistantSettings> = context.settingsDataStore.data.map { prefs ->
        val modeRaw = prefs[Keys.OPERATING_MODE]
        val mode = runCatching {
            if (modeRaw != null) OperatingMode.valueOf(modeRaw) else OperatingMode.PUSH_TO_TALK
        }.getOrDefault(OperatingMode.PUSH_TO_TALK)

        AssistantSettings(
            assistantName = prefs[Keys.ASSISTANT_NAME] ?: "يحيى",
            wakePhrase = prefs[Keys.WAKE_PHRASE] ?: "يا يحيى",
            speechLanguage = prefs[Keys.SPEECH_LANGUAGE] ?: "ar-SA",
            operatingMode = mode,
            geminiModelId = prefs[Keys.GEMINI_MODEL_ID]?.takeIf { it.isNotBlank() }
                ?: AssistantSettings.DEFAULT_GEMINI_MODEL,
            speechRate = (prefs[Keys.SPEECH_RATE] ?: 1.0f).coerceIn(0.6f, 1.8f),
            ttsVoiceName = prefs[Keys.TTS_VOICE_NAME]?.takeIf { it.isNotBlank() },
            webSearchEnabled = prefs[Keys.WEB_SEARCH_ENABLED] ?: true,
            foregroundServiceEnabled = prefs[Keys.FOREGROUND_SERVICE_ENABLED] ?: true,
            autoSpeakReplies = prefs[Keys.AUTO_SPEAK_REPLIES] ?: true,
            confirmSensitiveActions = prefs[Keys.CONFIRM_SENSITIVE_ACTIONS] ?: true,
            rtlEnabled = prefs[Keys.RTL_ENABLED] ?: true,
            systemOverlayEnabled = prefs[Keys.SYSTEM_OVERLAY_ENABLED] ?: false,
        )
    }

    suspend fun updateAssistantIdentity(assistantName: String, wakePhrase: String) {
        context.settingsDataStore.edit { prefs ->
            if (assistantName.isNotBlank()) prefs[Keys.ASSISTANT_NAME] = assistantName.trim()
            if (wakePhrase.isNotBlank()) prefs[Keys.WAKE_PHRASE] = wakePhrase.trim()
        }
    }

    suspend fun updateOperatingMode(mode: OperatingMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.OPERATING_MODE] = mode.name
        }
    }

    suspend fun updateGeminiModelId(modelId: String) {
        val clean = modelId.trim()
        if (clean.isBlank()) return
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.GEMINI_MODEL_ID] = clean
        }
    }

    suspend fun updateSpeechSettings(
        language: String,
        operatingMode: OperatingMode,
        foregroundServiceEnabled: Boolean,
        autoSpeakReplies: Boolean,
        speechRate: Float,
        ttsVoiceName: String?,
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SPEECH_LANGUAGE] = language
            prefs[Keys.OPERATING_MODE] = operatingMode.name
            prefs[Keys.FOREGROUND_SERVICE_ENABLED] = foregroundServiceEnabled
            prefs[Keys.AUTO_SPEAK_REPLIES] = autoSpeakReplies
            prefs[Keys.SPEECH_RATE] = speechRate.coerceIn(0.6f, 1.8f)
            if (ttsVoiceName.isNullOrBlank()) {
                prefs.remove(Keys.TTS_VOICE_NAME)
            } else {
                prefs[Keys.TTS_VOICE_NAME] = ttsVoiceName
            }
        }
    }

    suspend fun updateWebSearchEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.WEB_SEARCH_ENABLED] = enabled
        }
    }

    suspend fun updateSafetyAndUi(
        confirmSensitiveActions: Boolean,
        rtlEnabled: Boolean,
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.CONFIRM_SENSITIVE_ACTIONS] = confirmSensitiveActions
            prefs[Keys.RTL_ENABLED] = rtlEnabled
        }
    }

    suspend fun updateSystemOverlayEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SYSTEM_OVERLAY_ENABLED] = enabled
        }
    }
}
