package top.yudoge.vpad.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.repository.SettingRepository
import top.yudoge.vpad.toplevel.gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresetDomain @Inject constructor(
    private val settingRepository: SettingRepository
){
    val workingPresetJson: LiveData<String> = settingRepository.workingPreset.asLiveData()
    // convert LiveData<PresetJsonString> => LiveData<Preset>
    val workingPreset: LiveData<Preset> = Transformations.map(workingPresetJson) {
        gson.fromJson(it, Preset::class.java)
    }

    // 只允许外界通过Json串来修改Preset，发布给应用的Preset是完全不可变的，应用不可以修改其中的任何数据
    suspend fun updateWorkingPreset(presetJson: String) {
        settingRepository.updateWorkingPreset(presetJson)
    }


}