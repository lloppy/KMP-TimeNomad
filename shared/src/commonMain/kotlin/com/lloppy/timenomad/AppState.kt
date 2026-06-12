package com.lloppy.timenomad

import com.lloppy.timenomad.settings.ThemeMode

data class AppState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
