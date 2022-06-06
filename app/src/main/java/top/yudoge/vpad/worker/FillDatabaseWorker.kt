package top.yudoge.vpad.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yudoge.vpad.db.VPadDatabase
import top.yudoge.vpad.pojo.*
import java.lang.Exception

class FillDatabaseWorker (
    context: Context,
    workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val db = VPadDatabase.getInstance(applicationContext)
            val pads = (1..127)
            // 1~127个PadSetting

//            db.padSettingDao().insert(pads.map { PadSetting(it) })
//            db.padModeSettingDao().insert(pads.map { PadModeSetting(it) })
//            db.arpModeSettingDao().insert(pads.map { ArpModeSetting(it) })
//            db.chordModeSettingDao().insert(pads.map { ChordModeSetting(it) })
//            db.repeatModeSettingDao().insert(pads.map { RepeatModeSetting(it) })

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}