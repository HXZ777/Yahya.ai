package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.domain.model.AiConnectionTestResult
import com.example.domain.model.AssistantSettings
import com.example.domain.model.OperatingMode
import com.example.domain.model.TtsVoiceOption
import com.example.ui.theme.LocalYahyaColors
import java.util.Locale

private data class SpeechLocaleOption(val code: String, val label: String)

private val speechLocales = listOf(
    SpeechLocaleOption("ar-SA", "العربية (السعودية)"),
    SpeechLocaleOption("ar-EG", "العربية (مصر)"),
    SpeechLocaleOption("ar-AE", "العربية (الإمارات)"),
    SpeechLocaleOption("ar-JO", "العربية (الأردن)"),
    SpeechLocaleOption("en-US", "English (US)"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: AssistantSettings,
    hasApiKey: Boolean,
    maskedApiKey: String,
    isTestingConnection: Boolean,
    connectionTestResult: AiConnectionTestResult?,
    availableTtsVoices: List<TtsVoiceOption>,
    canDrawOverlays: Boolean,
    isSystemOverlayRunning: Boolean,
    onSaveIdentity: (assistantName: String, wakePhrase: String) -> Unit,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    onSaveGeminiModel: (String) -> Unit,
    onTestGeminiConnection: (overrideKey: String?, overrideModel: String?) -> Unit,
    onUpdateSpeechSettings: (
        language: String,
        operatingMode: OperatingMode,
        foregroundServiceEnabled: Boolean,
        autoSpeakReplies: Boolean,
        speechRate: Float,
        ttsVoiceName: String?,
    ) -> Unit,
    onUpdateWebSearchEnabled: (Boolean) -> Unit,
    onUpdateSafetyAndUi: (confirmSensitiveActions: Boolean, rtlEnabled: Boolean) -> Unit,
    onToggleSystemOverlay: (Boolean) -> Unit,
    onOpenControlScreen: () -> Unit,
    onClearConversation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val yColors = LocalYahyaColors.current
    var assistantName by rememberSaveable(settings.assistantName) { mutableStateOf(settings.assistantName) }
    var wakePhrase by rememberSaveable(settings.wakePhrase) { mutableStateOf(settings.wakePhrase) }
    var identitySaved by rememberSaveable { mutableStateOf(false) }

    var apiKeyInput by rememberSaveable { mutableStateOf("") }
    var showApiKey by rememberSaveable { mutableStateOf(false) }
    var keySavedBanner by rememberSaveable { mutableStateOf(false) }

    var modelInput by rememberSaveable(settings.geminiModelId) { mutableStateOf(settings.geminiModelId) }
    var speechRateSlider by rememberSaveable(settings.speechRate) { mutableFloatStateOf(settings.speechRate) }
    var chatClearedBanner by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                text = "إعدادات ${AssistantSettings.APP_OFFICIAL_NAME}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "تحكّم باسم المساعد الصوتي، كلمة التنبيه، مفتاح ونموذج Gemini API، الصوت والبحث في الإنترنت.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // 1. Assistant Identity & Wake Phrase
        item {
            SettingsCard(
                icon = Icons.Rounded.Person,
                iconTint = yColors.accentTeal,
                title = "هوية المساعد وكلمة التنبيه",
            ) {
                Text(
                    text = "اسم التطبيق الرسمي الثابت: ${AssistantSettings.APP_OFFICIAL_NAME} • يمكنك تخصيص اسم المساعد وكلمة التنبيه أدناه:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = assistantName,
                    onValueChange = { assistantName = it; identitySaved = false },
                    label = { Text("اسم المساعد الصوتي (الافتراضي: يحيى)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("settings_assistant_name_input"),
                )
                OutlinedTextField(
                    value = wakePhrase,
                    onValueChange = { wakePhrase = it; identitySaved = false },
                    label = { Text("كلمة التنبيه (الافتراضية: يا يحيى)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("settings_wake_phrase_input"),
                )
                Button(
                    onClick = {
                        onSaveIdentity(assistantName, wakePhrase)
                        identitySaved = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yColors.accentTeal,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_identity_button"),
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حفظ الاسم وكلمة التنبيه", fontWeight = FontWeight.Bold)
                }
                if (identitySaved) {
                    InlineStatusBanner("تم حفظ الاسم وكلمة التنبيه بنجاح", yColors.online, yColors.onlineContainer)
                }
            }
        }

        // 2. Gemini API Key & Model Configuration
        item {
            SettingsCard(
                icon = Icons.Rounded.AutoAwesome,
                iconTint = yColors.accentTeal,
                title = "الذكاء الاصطناعي (Gemini API)",
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Key, contentDescription = null, tint = if (hasApiKey) yColors.online else yColors.warning, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (hasApiKey) "مفتاح محفوظ ومشفّر: $maskedApiKey" else "لم يتم حفظ مفتاح Gemini API بعد",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasApiKey) yColors.online else yColors.warning,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it; keySavedBanner = false },
                    label = { Text("أدخل مفتاح Gemini API الجديد") },
                    placeholder = { Text("AIza...") },
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (showApiKey) "إخفاء المفتاح" else "إظهار المفتاح",
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("settings_api_key_input"),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (apiKeyInput.isNotBlank()) {
                                onSaveApiKey(apiKeyInput)
                                apiKeyInput = ""
                                keySavedBanner = true
                            }
                        },
                        enabled = apiKeyInput.isNotBlank(),
                        modifier = Modifier.weight(1f).height(48.dp).testTag("save_api_key_button"),
                    ) {
                        Text("حفظ المفتاح")
                    }
                    if (hasApiKey) {
                        OutlinedButton(
                            onClick = {
                                onClearApiKey()
                                keySavedBanner = false
                            },
                            modifier = Modifier.height(48.dp).testTag("clear_api_key_button"),
                        ) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "حذف المفتاح", tint = yColors.danger)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مسح", color = yColors.danger)
                        }
                    }
                }

                if (keySavedBanner) {
                    InlineStatusBanner("تم تشفير المفتاح وحفظه عبر Android Keystore.", yColors.online, yColors.onlineContainer)
                }

                Text(
                    text = "ملاحظة أمنية: يتم تشفير المفتاح محلياً على جهازك عبر Android Keystore ولا يُضمّن داخل الكود، والاتصال مصمم عبر بوابة قابلة للترقية لخادم وسيط لاحقاً.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "نموذج Gemini المستخدم",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AssistantSettings.RECOMMENDED_MODELS.forEach { preset ->
                        FilterChip(
                            selected = settings.geminiModelId == preset,
                            onClick = {
                                modelInput = preset
                                onSaveGeminiModel(preset)
                            },
                            label = { Text(preset) },
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = modelInput,
                        onValueChange = { modelInput = it },
                        label = { Text("معرّف النموذج (قابل للتغيير)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("settings_model_input"),
                    )
                    OutlinedButton(
                        onClick = { onSaveGeminiModel(modelInput) },
                        enabled = modelInput.isNotBlank() && modelInput.trim() != settings.geminiModelId,
                        modifier = Modifier.height(52.dp).testTag("save_model_button"),
                    ) {
                        Text("اعتماد")
                    }
                }

                OutlinedButton(
                    onClick = {
                        onTestGeminiConnection(
                            apiKeyInput.takeIf { it.isNotBlank() },
                            modelInput.takeIf { it.isNotBlank() },
                        )
                    },
                    enabled = !isTestingConnection,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("test_gemini_connection_button"),
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري اختبار الاتصال بـ Gemini...")
                    } else {
                        Icon(Icons.Rounded.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اختبار الاتصال بـ Gemini API", fontWeight = FontWeight.SemiBold)
                    }
                }

                connectionTestResult?.let { result ->
                    InlineStatusBanner(
                        message = result.detail,
                        accent = if (result.success) yColors.online else yColors.danger,
                        container = if (result.success) yColors.onlineContainer else yColors.danger.copy(alpha = 0.14f),
                    )
                }
            }
        }

        // 3. Operating Mode & Speech / TTS
        item {
            SettingsCard(
                icon = Icons.Rounded.RecordVoiceOver,
                iconTint = yColors.accentTeal,
                title = "وضع التشغيل والصوت (STT / TTS)",
            ) {
                Text(
                    text = "وضع الاستماع",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = settings.operatingMode == OperatingMode.PUSH_TO_TALK,
                        onClick = {
                            onUpdateSpeechSettings(
                                settings.speechLanguage,
                                OperatingMode.PUSH_TO_TALK,
                                settings.foregroundServiceEnabled,
                                settings.autoSpeakReplies,
                                settings.speechRate,
                                settings.ttsVoiceName,
                            )
                        },
                        label = { Text("وضع الضغط للتحدث") },
                        modifier = Modifier.testTag("mode_push_to_talk"),
                    )
                    FilterChip(
                        selected = settings.operatingMode == OperatingMode.WAKE_WORD,
                        onClick = {
                            onUpdateSpeechSettings(
                                settings.speechLanguage,
                                OperatingMode.WAKE_WORD,
                                settings.foregroundServiceEnabled,
                                settings.autoSpeakReplies,
                                settings.speechRate,
                                settings.ttsVoiceName,
                            )
                        },
                        label = { Text("وضع الاستدعاء (${settings.wakePhrase})") },
                        modifier = Modifier.testTag("mode_wake_word"),
                    )
                }

                Text(
                    text = "لهجة التعرف الصوتي",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    speechLocales.forEach { option ->
                        FilterChip(
                            selected = settings.speechLanguage == option.code,
                            onClick = {
                                onUpdateSpeechSettings(
                                    option.code,
                                    settings.operatingMode,
                                    settings.foregroundServiceEnabled,
                                    settings.autoSpeakReplies,
                                    settings.speechRate,
                                    settings.ttsVoiceName,
                                )
                            },
                            label = { Text(option.label) },
                        )
                    }
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("سرعة نطق الردود (TTS)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text(String.format(Locale.US, "%.1fx", speechRateSlider), style = MaterialTheme.typography.labelLarge, color = yColors.accentTeal)
                    }
                    Slider(
                        value = speechRateSlider,
                        onValueChange = { speechRateSlider = it },
                        onValueChangeFinished = {
                            onUpdateSpeechSettings(
                                settings.speechLanguage,
                                settings.operatingMode,
                                settings.foregroundServiceEnabled,
                                settings.autoSpeakReplies,
                                speechRateSlider,
                                settings.ttsVoiceName,
                            )
                        },
                        valueRange = 0.6f..1.8f,
                        steps = 5,
                        modifier = Modifier.testTag("tts_speed_slider"),
                    )
                }

                Text(
                    text = "الصوت العربي للنطق (TTS)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = settings.ttsVoiceName == null,
                        onClick = {
                            onUpdateSpeechSettings(
                                settings.speechLanguage,
                                settings.operatingMode,
                                settings.foregroundServiceEnabled,
                                settings.autoSpeakReplies,
                                settings.speechRate,
                                null,
                            )
                        },
                        label = { Text("الافتراضي التلقائي") },
                    )
                    availableTtsVoices.forEach { voice ->
                        FilterChip(
                            selected = settings.ttsVoiceName == voice.name,
                            onClick = {
                                onUpdateSpeechSettings(
                                    settings.speechLanguage,
                                    settings.operatingMode,
                                    settings.foregroundServiceEnabled,
                                    settings.autoSpeakReplies,
                                    settings.speechRate,
                                    voice.name,
                                )
                            },
                            label = { Text(voice.displayLabel) },
                        )
                    }
                }

                SettingToggleRow(
                    title = "تشغيل خدمة الميكروفون الأمامية",
                    subtitle = "يبقي الميكروفون نشطاً مع إشعار ظاهر وفق سياسات أندرويد 13.",
                    checked = settings.foregroundServiceEnabled,
                    onCheckedChange = {
                        onUpdateSpeechSettings(
                            settings.speechLanguage,
                            settings.operatingMode,
                            it,
                            settings.autoSpeakReplies,
                            settings.speechRate,
                            settings.ttsVoiceName,
                        )
                    },
                    testTag = "switch_foreground_service",
                )

                SettingToggleRow(
                    title = "نطق ردود المساعد صوتياً (TTS)",
                    subtitle = "قراءة إجابات ${settings.assistantName} بالعربية مع إيقاف الميكروفون مؤقتاً أثناء النطق.",
                    checked = settings.autoSpeakReplies,
                    onCheckedChange = {
                        onUpdateSpeechSettings(
                            settings.speechLanguage,
                            settings.operatingMode,
                            settings.foregroundServiceEnabled,
                            it,
                            settings.speechRate,
                            settings.ttsVoiceName,
                        )
                    },
                    testTag = "switch_auto_tts",
                )
            }
        }

        // 4. Web Search Grounding
        item {
            SettingsCard(
                icon = Icons.Rounded.Public,
                iconTint = yColors.accentTeal,
                title = "البحث في الإنترنت",
            ) {
                SettingToggleRow(
                    title = "تفعيل البحث الحي في الويب (Google Search)",
                    subtitle = "يسمح للمساعد بالبحث عن معلومات حديثة ومصادر موثقة عبر Gemini عند الطلب.",
                    checked = settings.webSearchEnabled,
                    onCheckedChange = onUpdateWebSearchEnabled,
                    testTag = "switch_web_search",
                )
            }
        }

        // 5. Safety, UI & Conversation Reset
        item {
            SettingsCard(
                icon = Icons.Rounded.Security,
                iconTint = yColors.warning,
                title = "الأمان والتحكم والمحادثة",
            ) {
                SettingToggleRow(
                    title = "تأكيد الإجراءات الحساسة قبل التنفيذ",
                    subtitle = "منع تنفيذ أوامر الدفع، الحذف، الإرسال، النشر، أو الكتابة دون موافقة.",
                    checked = settings.confirmSensitiveActions,
                    onCheckedChange = { onUpdateSafetyAndUi(it, settings.rtlEnabled) },
                    testTag = "switch_settings_confirm",
                )

                SettingToggleRow(
                    title = "فرض الاتجاه العربي (RTL)",
                    subtitle = "عرض الواجهة من اليمين لليسار بغض النظر عن لغة النظام.",
                    checked = settings.rtlEnabled,
                    onCheckedChange = { onUpdateSafetyAndUi(settings.confirmSensitiveActions, it) },
                    testTag = "switch_rtl_mode",
                )

                SettingToggleRow(
                    title = "الزر العائم فوق جميع التطبيقات",
                    subtitle = "إظهار زر الميكروفون العائم فوق أي تطبيق في النظام.",
                    checked = settings.systemOverlayEnabled && canDrawOverlays && isSystemOverlayRunning,
                    onCheckedChange = onToggleSystemOverlay,
                    testTag = "switch_settings_system_overlay",
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenControlScreen,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) {
                        Text("صلاحيات التحكم")
                    }
                    OutlinedButton(
                        onClick = {
                            onClearConversation()
                            chatClearedBanner = true
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("clear_conversation_button"),
                    ) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = null, tint = yColors.danger, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مسح المحادثة", color = yColors.danger)
                    }
                }

                if (chatClearedBanner) {
                    InlineStatusBanner("تم مسح سجل المحادثة وسياق الذاكرة.", yColors.accentTeal, yColors.accentTealSoft)
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val yColors = LocalYahyaColors.current
    Card(
        colors = CardDefaults.cardColors(containerColor = yColors.surfaceCard),
        border = BorderStroke(1.dp, yColors.borderSubtle),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            content()
        }
    }
}

@Composable
private fun InlineStatusBanner(
    message: String,
    accent: Color,
    container: Color,
) {
    Surface(
        color = container,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = accent)
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
        )
    }
}
