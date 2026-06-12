package com.lloppy.timenomad

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lloppy.timenomad.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Time Nomad",
        ) {
            App()
        }
    }
}
