package top.yudoge.vpad.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpad.toplevel.dataStore
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
){

//    // remove this
//    private val defaultPadSetting by lazy {
//        InputStreamReader(context.assets.open(Constants.DEFAULT_PAD_SETTING_FILE_NAME)).readText()
//    }
//    // remove this
//    val padSettings: Flow<String> = context.dataStore.data
//        .map {
//            it[PAD_SETTING_KEY] ?: defaultPadSetting
//        }
//

    private val defaultPreset by lazy {
        InputStreamReader(context.assets.open(Constants.DEFAULT_PRESET_FILE_NAME)).readText()
    }

    // 当前工作Preset，这是一个文本格式的JSON
    val workingPreset: Flow<String> = context.dataStore.data
        .map {
            it[WORKING_PRESET_KEY] ?: defaultPreset
        }


    val bpm: Flow<Int> = context.dataStore.data
        .map {
            it[BPM_KEY] ?: Constants.DEFAULT_BPM
        }

    val baseNote: Flow<Int> = context.dataStore.data
        .map {
            it[BASE_NOTE_KEY] ?: Constants.DEFAULT_BASE
        }

    /**
     * preset must be a vaild json string
     */
    suspend fun updateWorkingPreset(preset: String) {
        context.dataStore.edit {
            it[WORKING_PRESET_KEY] = preset
        }
    }

    /**
     * padSetting must be a vaild json string
     */
//    suspend fun updatePadSetting(padSetting: String) {
//        context.dataStore.edit {
//            it[PAD_SETTING_KEY] = padSetting
//        }
//    }

    /**
     * bpm must in range [1, 65535]
     */
    suspend fun updateBpm(bpm: Int) {
        context.dataStore.edit {
            it[BPM_KEY] = bpm
        }
    }

    /**
     * 基于BaseNote为第一个打击垫，其最后一个打击垫必须不能大于127，第一个打击垫必定不能小于0
     */
    suspend fun increaseBaseNote(incr: Int): Boolean {
        var result = true
        context.dataStore.edit {
            val curr = it[BASE_NOTE_KEY] ?: Constants.DEFAULT_BASE
            val next = curr + incr
            if (next + 15 > 127 || next < 0) {
                result = false;
                return@edit
            }
            it[BASE_NOTE_KEY] = curr + incr
        }
        return result
    }

    companion object {
        // remove this
//        private val PAD_SETTING_KEY = stringPreferencesKey("pad_setting_json")

        private val WORKING_PRESET_KEY = stringPreferencesKey("working_preset")
        private val BPM_KEY = intPreferencesKey("bpm")
        private val BASE_NOTE_KEY = intPreferencesKey("base_note")
        const val TAG = "SettingRepository"
    }
}