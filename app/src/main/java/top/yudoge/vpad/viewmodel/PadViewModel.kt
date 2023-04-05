package top.yudoge.vpad.viewmodel

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.*
import top.yudoge.vpad.domain.PadSettingDomain
import top.yudoge.vpad.domain.WorkingPresetDomain
import top.yudoge.vpad.pojo.*
import top.yudoge.vpad.repository.PresetRepository
import top.yudoge.vpad.repository.SettingRepository
import top.yudoge.vpad.toplevel.gson
import top.yudoge.vpad.toplevel.replace
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.*
import java.io.File
import javax.inject.Inject

/**
 * 该ViewModel主要负责与服务器交互
 */
@HiltViewModel
class PadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workingPresetDomain: WorkingPresetDomain,
    private val presetRepository: PresetRepository,
    private val settingRepository: SettingRepository,
    private val controlMessageViewmodel: ControlMessageViewmodel
) : ViewModel() {

    // 关于Server
    private val vpadServer = savedStateHandle.get<VPadServer>(VPAD_SERVER_KEY)!!
    private val _serverLabel: MutableLiveData<String> = MutableLiveData("${vpadServer.name}")
    val serverLabel: LiveData<String> by ::_serverLabel

    private val _screenMessage: MutableLiveData<String> = MutableLiveData("No Message")
    val screenMessage: LiveData<String> by ::_screenMessage

    // 关于设置项
    val bpm: LiveData<Int> = settingRepository.bpm.asLiveData()
    val workingPreset: LiveData<Preset> = workingPresetDomain.workingPreset;
    private var _settingMode: MutableLiveData<Boolean> = MutableLiveData(false);
    val settingMode: LiveData<Boolean> by ::_settingMode

    // 私有的，外部不可以访问，但是可以增删数据
    private val _copyToPadIndexies: MutableList<Pair<Int, View>> = mutableListOf()
    // 外部可以公开访问 但是不可变
    val copyToPadIndexies: List<Pair<Int, View>> = _copyToPadIndexies
    var currentDragItem: Int = -1

    fun addCopyPad(index: Int, view: View) {
        _copyToPadIndexies.add(Pair(index, view))
    }
    fun containsCopyPad(index: Int, view: View) = _copyToPadIndexies.contains(Pair(index, view))
    fun clearCopyPads() {
        _copyToPadIndexies.clear();
    }
    fun removeCopyPad(index: Int, view: View) {
        _copyToPadIndexies.remove(Pair(index, view))
    }

    fun setBpm(bpm: Int) = viewModelScope.launch {
        settingRepository.updateBpm(bpm)
        _screenMessage.value = "bpm -> ${bpm}"
    }

    fun updatePadsPerLineAndRegionSpan(newPadsPerLine: Int, newRegionSpan: Int) = viewModelScope.launch {
        workingPresetDomain.updatePadsPerLineAndRegionSpan(newPadsPerLine, newRegionSpan)
    }

    fun setPadsPerLine(newPadsPerLine: Int) = viewModelScope.launch {
        workingPresetDomain.updatePadsPerLine(newPadsPerLine)
    }
    fun setRegionSpan(regionSpan: Int) = viewModelScope.launch {
        workingPresetDomain.updateRegionSpan(regionSpan)
    }

    fun updatePadSettingsBatch() = viewModelScope.launch {
        if (currentDragItem != -1 && copyToPadIndexies.size != 0) {
            val jo = JsonParser.parseString(workingPresetDomain.workingPresetJson.value!!).asJsonObject
            val padSettings = jo["padSettings"].asJsonArray
            val sourceSetting = padSettings[currentDragItem].asJsonObject
            for (i in copyToPadIndexies) {
                if (currentDragItem == i.first) continue
                val targetSetting = padSettings[i.first].asJsonObject
                targetSetting.replace("velocity", sourceSetting["velocity"])
                targetSetting.replace("mode", sourceSetting["mode"])
                targetSetting.replace("specificModeSetting", sourceSetting["specificModeSetting"])
                padSettings[i.first] = targetSetting
            }
            jo.replace("padSettings", padSettings)
            workingPresetDomain.updateWorkingPreset(gson.toJson(jo))
        }
    }

    fun deletePadAt(index: Int) = viewModelScope.launch {
        val jo = JsonParser.parseString(workingPresetDomain.workingPresetJson.value!!).asJsonObject
        val padSettings = jo["padSettings"].asJsonArray
        padSettings.remove(index)
        jo.replace("padSettings", padSettings)
        workingPresetDomain.updateWorkingPreset(gson.toJson(jo))
    }


    fun appendPad() = viewModelScope.launch {
        val jo = JsonParser.parseString(workingPresetDomain.workingPresetJson.value!!).asJsonObject
        val padSettings = jo["padSettings"].asJsonArray
        padSettings.add(gson.toJsonTree(PadSetting(0, "NewPad", PadMode.Pad, 90, PadModeSetting())))
        jo.replace("padSettings", padSettings)
        workingPresetDomain.updateWorkingPreset(gson.toJson(jo))
    }

    fun getMessageByPadState(state: Int, padSetting: PadSetting, bpm: Int, baseNote: Int) : Message {

        _screenMessage.value = "Pad ${padSetting.title} ${if(state == MidiMessage.STATE_ON) "ON" else "OFF"}, note ${baseNote + padSetting.offset} "

//        Log.d("PadViewModel", "PadSetting => " + padSetting)
        val note = baseNote + padSetting.offset
        val subSetting = padSetting.specificModeSetting
        when (padSetting.mode) {
            PadMode.Pad -> return MidiMessage(note, padSetting.velocity, state)
            PadMode.Repeat -> {
                subSetting as RepeatModeSetting
                return ArpMessage(note, padSetting.velocity, state, ArpMethod.NO_METHOD.ordinal, subSetting.rate.ordinal, subSetting.swingPct, 1, subSetting.velocityAutomation.ordinal, subSetting.dynamicPct, bpm)
            }
            PadMode.Arp -> {
                subSetting as ArpModeSetting
                return ArpMessage(note, padSetting.velocity, state, subSetting.method.ordinal, subSetting.rate.ordinal, subSetting.swingPct, subSetting.upNoteCnt, subSetting.velocityAutomation.ordinal, subSetting.dynamicPct, bpm)
            }
            PadMode.Chord -> {
                subSetting as ChordModeSetting
                return ChordMessage(note, padSetting.velocity, state, bpm, subSetting.type.ordinal, subSetting.level.ordinal, subSetting.transpose, subSetting.arpPct)
            }
        }
    }

    fun getControlMessageByLabel(label: String) : ControlMessage? {
        _screenMessage.value = label
         return when(label) {
            "PLAY" -> controlMessageViewmodel.playMessage
            "STOP" -> controlMessageViewmodel.stopMessage
            "REC" -> controlMessageViewmodel.recordMessage
            "LOOP" -> controlMessageViewmodel.loopMessage
            "UNDO" -> controlMessageViewmodel.undoMessage
            "REDO" -> controlMessageViewmodel.redoMessage
            "CLK" -> controlMessageViewmodel.clickMessage
            "SAVE" -> controlMessageViewmodel.saveMessage
            else -> null
        }
    }


    fun increaseNoteRegion() = viewModelScope.launch {
        _screenMessage.value = "Rgn +" + workingPresetDomain.increaseBaseNote()
    }

    fun decreaseNoteRegion() = viewModelScope.launch {
        _screenMessage.value = "Rgn -" + Math.abs(workingPresetDomain.decreaseBaseNote())
    }

    fun openSettingMode() {
        _settingMode.value = true
        _screenMessage.value = "setting mode opened"
    }
    fun toggleSettingMode() {
        _settingMode.value = !_settingMode.value!!
    }

    fun closeSettingMode() {
        _settingMode.value = false
        _screenMessage.value = "setting mode closed"
    }

    fun exportWorkingPreset(name: String, author: String, description: String): File {
        val preset = workingPresetDomain.workingPreset.value!!
        return presetRepository.addPreset(preset.newPresetFromThis(name, author, description))
    }

    companion object {
        const val VPAD_SERVER_KEY = "vpadServer"
    }

}