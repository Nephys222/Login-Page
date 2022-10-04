package com.nilearning.palace

import android.app.Application
import com.nilearning.palace.util.PreferenceHelper

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        PreferenceHelper.initialize(this)
    }
}