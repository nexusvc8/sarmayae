package com.example.ui.localization

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flag: String,
    val nativeName: String,
    val englishName: String,
    val subtitle: String
) {
    ENGLISH(
        code = "en",
        displayName = "English",
        flag = "🇺🇸",
        nativeName = "English",
        englishName = "English",
        subtitle = "English Language (Left-to-Right)"
    ),
    URDU(
        code = "ur",
        displayName = "اردو",
        flag = "🇵🇰",
        nativeName = "اردو",
        englishName = "Urdu",
        subtitle = "قومی زبان اردو (دائیں سے بائیں)"
    );

    val isRtl: Boolean
        get() = this == URDU
}
