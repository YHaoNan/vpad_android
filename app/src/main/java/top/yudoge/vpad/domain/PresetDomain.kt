package top.yudoge.vpad.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import top.yudoge.vpad.VPadApplication
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.pojo.PresetRecord
import top.yudoge.vpad.repository.PresetFileRepository
import top.yudoge.vpad.repository.PresetRecordDao
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PresetDomain将数据库中的PresetRecord以及文件系统中的Preset实体操作整合到了一起
 * 可以看作是一个业务层，它会维护二者之间的一致性
 */
@Singleton
class PresetDomain @Inject constructor(
    private val presetFileRepository: PresetFileRepository,
    private val presetRecordDao: PresetRecordDao
){

    suspend fun addPreset(preset: Preset) {
        val file = presetFileRepository.addPreset(preset, preset.generateFileName())
        withContext(Dispatchers.IO) {
            presetRecordDao.insert(preset.toPresetRecord(file.name))
        }
    }

    suspend fun deletePreset(presetRecord: PresetRecord) {
        withContext(Dispatchers.IO) {
            presetRecordDao.delete(presetRecord)
        }
        presetFileRepository.deletePreset(presetRecord.toPresetWithoutReadPresetFileFile())
    }

    suspend fun readPreset(presetRecord: PresetRecord): Preset {
        return presetFileRepository.readPresetFile(presetRecord.targetFile)
    }

    fun getAllPresetRecordOrderByName(subStringInName: String): Flow<List<PresetRecord>> {
        return presetRecordDao.getAllOrderByName(subStringInName)
    }

    fun getAllPresetRecordOrderByCreateTime(subStringInName: String): Flow<List<PresetRecord>> {
        return presetRecordDao.getAllOrderByCreateTime(subStringInName)
    }

    private fun Preset.generateFileName(): String {
        return "${this.presetName}.preset.json"
    }

}