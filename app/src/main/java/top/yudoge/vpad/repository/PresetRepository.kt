package top.yudoge.vpad.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import top.yudoge.vpad.exceptions.PresetAddException
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.toplevel.gson
import top.yudoge.vpad.toplevel.safeGetPresetDir
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 从文件夹读取Presets
 */
@Singleton
class PresetRepository @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val _presets: MutableLiveData<List<Preset>> = MutableLiveData(listOf())
    val presets: LiveData<List<Preset>> = _presets

    /**
     * 刷新当前的presets，用户可能添加了新的preset或首次读取
     * 调用该方法时，presets目录可能不存在，所以该方法必须正确建立目录
     * 该方法一定在文件读写权限被正确授予时才会被调用
     *
     * 返回的数据按照最后修改时间降序排序
     */
    fun flushPresets() {

        val newList = safeGetPresetDir(context).listFiles()
            .filter { it.extension.equals("json") && it.isFile }
            .sortedByDescending { it.lastModified() }
            .map {
                try {
                    gson.fromJson(FileReader(it), Preset::class.java)
                } catch (e: Throwable) {
                    null
                }
            }
            .filterNotNull()
            .toList()

        _presets.value = newList
    }


    /**
     * 添加一个Preset，调用该方法时，preset目录可能不存在，所以该方法必须正确建立目录
     * 本类使用${preset.presetName}.preset.json作为preset持久化的文件名
     * 该方法一定在文件读写权限被正确授予时才会被调用
     * @throws PresetAddException 添加preset失败
     * @throws IOException
     */
    fun addPreset(preset: Preset) {
        addPresetByJson(gson.toJson(preset), preset.presetName, true)
    }

    /**
     * addPreset
     * 但不刷新，preset这个livedata不会得到更新
     */
    fun addPresetSilent(preset: Preset) {
        addPresetByJson(gson.toJson(preset), preset.presetName, false)
    }

    // 除了能够确保正确的builtin presets，其它preset不可以调用该方法
    fun addPresetByJson(json: String, presetName: String, flush: Boolean) {
        val presetFile = File(safeGetPresetDir(context), "${presetName}.preset.json")
        if (presetFile.exists()) throw PresetAddException("preset ${presetName} 已经存在")
        presetFile.createNewFile()

        val writer = FileWriter(presetFile)
        writer.write(json)
        writer.flush()
        writer.close()

        if (flush) flushPresets()
    }


    fun deletePreset(preset: Preset) {
        val presetFile = File(safeGetPresetDir(context), "${preset.presetName}.preset.json")
        if (presetFile.exists()) presetFile.delete()
        flushPresets()
    }

    fun deletePresetSilent(preset: Preset) {
        val presetFile = File(safeGetPresetDir(context), "${preset.presetName}.preset.json")
        if (presetFile.exists()) presetFile.delete()
    }



}