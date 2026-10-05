package com.example

import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testAppLanguageProperties() {
        assertTrue(AppLanguage.URDU.isRtl)
        assertFalse(AppLanguage.ENGLISH.isRtl)
        assertEquals("ur", AppLanguage.URDU.code)
        assertEquals("en", AppLanguage.ENGLISH.code)
        assertEquals("🇵🇰", AppLanguage.URDU.flag)
        assertEquals("🇺🇸", AppLanguage.ENGLISH.flag)
    }

    @Test
    fun testStringsManagerLocalization() {
        val appNameEn = StringsManager.get("app_name", AppLanguage.ENGLISH)
        val appNameUr = StringsManager.get("app_name", AppLanguage.URDU)
        assertEquals("Sarmaya Invest", appNameEn)
        assertEquals("سرمایہ انویسٹ", appNameUr)

        val switchTitleEn = StringsManager.get("language_switcher_title", AppLanguage.ENGLISH)
        val switchTitleUr = StringsManager.get("language_switcher_title", AppLanguage.URDU)
        assertEquals("Select Language", switchTitleEn)
        assertEquals("زبان منتخب کریں", switchTitleUr)
    }
}
