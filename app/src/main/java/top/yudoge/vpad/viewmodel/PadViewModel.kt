package top.yudoge.vpad.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.*
import top.yudoge.vpad.domain.PadSettingDomain
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
    val padSettings: LiveData<List<PadSetting>> = padSettingDomain.padSettings
    private var _settingMode = false
    val settingMode by ::_settingMode

    private var _noteOffset = 35
    val noteOffset by ::_noteOffset

    fun setBpm(bpm: Int) = viewModelScope.launch {
        settingRepository.updateBpm(bpm)
        _screenMessage.value = "bpm -> ${bpm}"
    }

    fun getMessageByPadState(padId: Int, state: Int, padSetting: PadSetting, bpm: Int) : Message {
        _screenMessage.value = "Pad ${padId} ${if(state == MidiMessage.STATE_ON) "ON" else "OFF"}, note ${noteOffset + padId} "
        val note = noteOffset + padId
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
                return MidiMessage(note, padSetting.velocity, state)
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


    fun increaseNoteRegion() {
        // _noteOffset + 16 是屏幕中的最大note，需要在此基础再加16
        if (_noteOffset + 16 + 16 < 127){
            _noteOffset += 16
            _screenMessage.value = "note region to [${noteOffset + 1}, ${noteOffset + 17}]"
        } else {
            _screenMessage.value = "can't increase region"
        }
    }

    fun decreaseNoteRegion() {
        if (_noteOffset > 16) {
            _noteOffset -= 16
            _screenMessage.value = "note region to [${noteOffset + 1}, ${noteOffset + 17}]"
        } else {
            _screenMessage.value = "can't decrease region"
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