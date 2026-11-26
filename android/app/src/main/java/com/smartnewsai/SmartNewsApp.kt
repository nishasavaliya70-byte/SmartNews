package com.smartnewsai

import android.app.Application

class SmartNewsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SmartNewsApp
            private set
    }
}
