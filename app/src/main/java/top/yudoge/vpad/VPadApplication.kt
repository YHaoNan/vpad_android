package top.yudoge.vpad

import android.app.Application
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VPadApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}