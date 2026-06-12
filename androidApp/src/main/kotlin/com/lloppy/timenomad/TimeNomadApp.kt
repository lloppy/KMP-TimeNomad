package com.lloppy.timenomad

import android.app.Application
import com.lloppy.timenomad.di.initKoin
import org.koin.android.ext.koin.androidContext

class TimeNomadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TimeNomadApp)
        }
    }
}
