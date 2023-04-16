package top.yudoge.vpad.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import top.yudoge.vpad.pojo.PresetRecord
import top.yudoge.vpad.repository.PresetRecordDao
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpad.worker.FillDatabaseWorker

@Database(
    entities = arrayOf(PresetRecord::class),
    version = 1
)
abstract class VPadDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: VPadDatabase? = null

        fun getInstance(
            context: Context
        ) = synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context, VPadDatabase::class.java, Constants.DB_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<FillDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build().also { INSTANCE = it }
        }

    }

    abstract fun presetRecordDao(): PresetRecordDao
}