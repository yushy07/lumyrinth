package com.lumyrinth.app

import android.app.Application
import com.lumyrinth.app.di.AppContainer

class LumyrinthApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
