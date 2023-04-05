package top.yudoge.vpad.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.domain.WorkingPresetDomain
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.repository.PresetRepository
import top.yudoge.vpad.toplevel.gson
import javax.inject.Inject

@HiltViewModel
class PresetViewModel @Inject constructor(
    private val presetRepository: PresetRepository,
    private val workingPresetViewModel: WorkingPresetDomain
) : ViewModel() {
    val preset: LiveData<List<Preset>> = presetRepository.presets

    fun flushPresets() {
        presetRepository.flushPresets()
    }

    fun setWorkingPreset(preset: Preset) {
        viewModelScope.launch {
            workingPresetViewModel.updateWorkingPreset(gson.toJson(preset))
        }
    }
    fun deletePreset(preset: Preset) {
        viewModelScope.launch {
            presetRepository.deletePreset(preset)
        }
    }

}