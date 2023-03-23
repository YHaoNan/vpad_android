package top.yudoge.vpad.viewmodel

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.*
import top.yudoge.vpad.domain.PadSettingDomain
import top.yudoge.vpad.domain.PresetDomain
import top.yudoge.vpad.pojo.*
import top.yudoge.vpad.repository.SettingRepository
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.*
import javax.inject.Inject

/**
 * 该ViewModel主要负责与服务器交互
 */
@HiltViewModel
class PadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val padSettingDomain: PadSettingDomain,
    private val presetDomain: PresetDomain,
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
    val workingPreset: LiveData<Preset> = presetDomain.workingPreset;
    private var _settingMode = false
    val settingMode by ::_settingMode

//    val baseNote: LiveData<Int> = settingRepository.baseNote.asLiveData()
//    val padSettings: LiveData<List<PadSetting>> = padSettingDomain.padSettings

    fun setBpm(bpm: Int) = viewModelScope.launch {
        settingRepository.updateBpm(bpm)
        _screenMessage.value = "bpm -> ${bpm}"
    }

    fun getMessageByPadState(state: Int, padSetting: PadSetting, bpm: Int, baseNote: Int) : Message {

        _screenMessage.value = "Pad ${padSetting.title} ${if(state == MidiMessage.STATE_ON) "ON" else "OFF"}, note ${baseNote + padSetting.offset} "

        Log.i("PadViewModel", "PadSetting => " + padSetting)
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
        if (settingRepository.increaseBaseNote(16)) {
            _screenMessage.value = "Region + 16"
        } else {
            _screenMessage.value = "Limited"
        }
    }

    fun decreaseNoteRegion() = viewModelScope.launch {
        if (settingRepository.increaseBaseNote(-16)) {
            _screenMessage.value = "Region - 16"
        } else {
            _screenMessage.value = "Limited"
        }
    }

    fun openSettingMode() {
        _settingMode = true
        _screenMessage.value = "setting mode opened"
    }

    fun closeSettingMode() {
        _settingMode = false
        _screenMessage.value = "setting mode closed"
    }

    companion object {
        const val VPAD_SERVER_KEY = "vpadServer"
    }

}