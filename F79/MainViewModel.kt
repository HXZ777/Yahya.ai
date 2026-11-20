package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.YahyaCompanionApp
import com.example.domain.model.AccessibilityHealth
import com.example.domain.model.AiConnectionTestResult
import com.example.domain.model.AiStatus
import com.example.domain.model.AssistantSettings
import com.example.domain.model.CommandExecutionLog
import com.example.domain.model.ConversationMessage
import com.example.domain.model.MicStatus
import com.example.domain.model.OperatingMode
import com.example.domain.model.PendingConfirmation
import com.example.domain.model.TtsVoiceOption
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val coordinator = (application as YahyaCompanionApp).coordinator

    val settings: StateFlow<AssistantSettings> = coordinator.settings
    val hasApiKey: StateFlow<Boolean> = coordinator.hasApiKey
    val maskedApiKey: StateFlow<String> = coordinator.maskedApiKey
    val hasInternet: StateFlow<Boolean> = coordinator.hasInternet
    val assistantPowerEnabled: StateFlow<Boolean> = coordinator.assistantPowerEnabled
    val aiStatus: StateFlow<AiStatus> = coordinator.aiStatus
    val lastAiError: StateFlow<String?> = coordinator.lastAiError
    val isTestingConnection: StateFlow<Boolean> = coordinator.isTestingConnection
    val connectionTestResult: StateFlow<AiConnectionTestResult?> = coordinator.connectionTestResult

    val micStatus: StateFlow<MicStatus> = coordinator.micStatus
    val isMicListening: StateFlow<Boolean> = coordinator.isMicListening
    val partialTranscript: StateFlow<String> = coordinator.partialTranscript
    val rmsLevel: StateFlow<Float> = coordinator.rmsLevel
    val isForegroundServiceRunning: StateFlow<Boolean> = coordinator.isForegroundServiceRunning
    val isSystemOverlayRunning: StateFlow<Boolean> = coordinator.isSystemOverlayRunning
    val canDrawOverlays: StateFlow<Boolean> = coordinator.canDrawOverlays
    val availableTtsVoices: StateFlow<List<TtsVoiceOption>> = coordinator.availableTtsVoices

    val conversation: StateFlow<List<ConversationMessage>> = coordinator.conversation
    val logs: StateFlow<List<CommandExecutionLog>> = coordinator.logs
    val pendingConfirmation: StateFlow<PendingConfirmation?> = coordinator.pendingConfirmation
    val accessibilityHealth: StateFlow<AccessibilityHealth> = coordinator.accessibilityHealth

    val deviceModelLabel: String = coordinator.deviceModelLabel

    fun refreshDeviceHealth() = coordinator.refreshDeviceHealth()
    fun setAssistantPower(enabled: Boolean) = coordinator.setAssistantPower(enabled)
    fun toggleAssistantPower() = coordinator.toggleAssistantPower()

    fun toggleMicrophone() = coordinator.toggleMicrophone()
    fun toggleDirectMicrophone() = coordinator.toggleDirectMicrophone()
    fun startMicrophone() = coordinator.startMicrophone()
    fun startDirectListening() = coordinator.startDirectListening()
    fun stopMicrophone() = coordinator.stopMicrophone()
    fun logMicPermissionDenied() = coordinator.logMicPermissionDenied()
    fun sendManualTranscript(text: String) = coordinator.sendManualTextTranscript(text)

    fun approvePendingConfirmation() = coordinator.approvePendingConfirmation()
    fun rejectPendingConfirmation() = coordinator.rejectPendingConfirmation()

    fun saveApiKey(rawKey: String) = coordinator.saveApiKey(rawKey)
    fun clearApiKey() = coordinator.clearApiKey()
    fun testAiConnection(overrideKey: String? = null, overrideModelId: String? = null) =
        coordinator.testAiConnection(overrideKey, overrideModelId)

    fun updateAssistantIdentity(name: String, wakePhrase: String) =
        coordinator.updateAssistantIdentity(name, wakePhrase)

    fun updateOperatingMode(mode: OperatingMode) =
        coordinator.updateOperatingMode(mode)

    fun updateGeminiModelId(modelId: String) =
        coordinator.updateGeminiModelId(modelId)

    fun updateSpeechSettings(
        language: String,
        operatingMode: OperatingMode,
        foregroundServiceEnabled: Boolean,
        autoSpeakReplies: Boolean,
        speechRate: Float,
        ttsVoiceName: String?,
    ) = coordinator.updateSpeechSettings(
        language = language,
        operatingMode = operatingMode,
        foregroundServiceEnabled = foregroundServiceEnabled,
        autoSpeakReplies = autoSpeakReplies,
        speechRate = speechRate,
        ttsVoiceName = ttsVoiceName,
    )

    fun updateWebSearchEnabled(enabled: Boolean) =
        coordinator.updateWebSearchEnabled(enabled)

    fun updateSafetyAndUi(confirmSensitiveActions: Boolean, rtlEnabled: Boolean) =
        coordinator.updateSafetyAndUi(confirmSensitiveActions, rtlEnabled)

    fun setSystemOverlayEnabled(enabled: Boolean) = coordinator.setSystemOverlayEnabled(enabled)

    fun clearConversationAndLogs() = coordinator.clearConversationAndLogs()
    fun testSafeUrlPreview() = coordinator.testSafeUrlPreview()
    fun testSensitiveGuardrailPreview() = coordinator.testSensitiveGuardrailPreview()
}
