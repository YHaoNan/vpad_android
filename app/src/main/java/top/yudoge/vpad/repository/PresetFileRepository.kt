package top.yudoge.vpad.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.qualifiers.ApplicationContext
import top.yudoge.vpad.exceptions.PresetException
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.toplevel.gson
import top.yudoge.vpad.toplevel.safeGetPresetDir
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 代表一个文件形式的Preset
 * 用户（甚至Domain）不应该直接操作PresetFileRepository，而是使用PresetDomain
 */
@Singleton
class PresetFileRepository @Inject constructor(
    @ApplicationContext val context: Context
) {

    /**
     * @throws PresetException 添加preset失败
     * @throws IOException
     */
    fun addPreset(preset: Preset, fileName: String): File {
        return addPresetByJson(gson.toJson(preset), fileName)
    }


    // 除了能够确保正确的builtin presets，其它preset不可以调用该方法
    private fun addPresetByJson(json: String, fileName: String): File {
        val presetFile = File(safeGetPresetDir(context), fileName)
        if (presetFile.exists()) throw PresetException("preset ${fileName} 已经存在")
        presetFile.createNewFile()

        val writer = FileWriter(presetFile)
        writer.write(json)
        writer.flush()
        writer.close()

        return presetFile
    }

    /**
     * @throws PresetException 读取Preset失败
     * @throws IOException
     */
    fun readPresetFile(fileName: String): Preset {
        val presetFile = File(safeGetPresetDir(context), fileName)
        if (!presetFile.exists()) throw PresetException("preset ${fileName} 不存在")

        val reader = FileReader(presetFile)
        val content = reader.readText()
        try {
            return gson.fromJson(content, Preset::class.java)
        } catch (e: JsonSyntaxException) {
            throw PresetException("preset ${fileName} 已经损坏")
        }
    }


    /**
     * @throws IOException
     */
    fun deletePreset(preset: Preset) {
        val presetFile = File(safeGetPresetDir(context), "${preset.presetName}.preset.json")
        if (presetFile.exists()) presetFile.delete()
    }

}