package top.yudoge.vpad.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import top.yudoge.vpad.domain.PresetDomain
import top.yudoge.vpad.domain.WorkingPresetDomain
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.pojo.PresetRecord
import top.yudoge.vpad.repository.PresetFileRepository
import top.yudoge.vpad.toplevel.gson
import javax.inject.Inject

@HiltViewModel
class PresetViewModel @Inject constructor(
    private val presetDomain: PresetDomain,
    private val workingPresetViewModel: WorkingPresetDomain
) : ViewModel() {

    fun getAllPresetRecordOrderByName(subStringInName: String): Flow<List<PresetRecord>> {
        return presetDomain.getAllPresetRecordOrderByName(subStringInName)
    }

    fun getAllPresetRecordOrderByCreateTime(subStringInName: String): Flow<List<PresetRecord>> {
        return presetDomain.getAllPresetRecordOrderByCreateTime(subStringInName)
    }

    fun setWorkingPreset(presetRecord: PresetRecord) {
        viewModelScope.launch {
            val preset = presetDomain.readPreset(presetRecord)
            workingPresetViewModel.updateWorkingPreset(gson.toJson(preset))
        }
    }

    fun importPreset(preset: Preset) {
        viewModelScope.launch {
            presetDomain.addPreset(preset)
        }
    }

    fun deletePreset(preset: PresetRecord) {
        viewModelScope.launch {
            presetDomain.deletePreset(preset)
        }
    }

}