package top.yudoge.vpad

import android.app.Application
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import top.yudoge.vpad.db.VPadDatabase
import top.yudoge.vpad.repository.PresetRecordDao

@HiltAndroidApp
class VPadApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}