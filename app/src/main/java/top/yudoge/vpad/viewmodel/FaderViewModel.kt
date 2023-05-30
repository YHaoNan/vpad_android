package top.yudoge.vpad.viewmodel

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.CommunicatorHolder
import top.yudoge.vpad.api.TrackStateEnums
import top.yudoge.vpad.domain.WorkingPresetDomain
import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode
import top.yudoge.vpad.repository.SettingRepository
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.Message
import top.yudoge.vpadapi.structure.TrackMessage
import javax.inject.Inject


@HiltViewModel
class FaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingRepository: SettingRepository,
    private val workingPresetDomain: WorkingPresetDomain,
    private val controlMessageViewmodel: ControlMessageViewmodel
): ViewModel() {
    private val vPadServer = savedStateHandle.get<VPadServer>(VPAD_SERVER_KEY)!!

    private val _trackMode = MutableLiveData(true)
    val trackMode by ::_trackMode

    private val ccFaders: LiveData<List<Fader>> = Transformations.map(settingRepository.ccList.asLiveData()) {
        it.split(",").mapIndexed {i, s ->
            Fader(i, 97, FaderMode.CC, s.toInt())
        }
    }
    private val trackFaders = MutableLiveData(Constants.DEFAULT_TRACK_FADERS.value)
    val faders: MediatorLiveData<List<Fader>> = MediatorLiveData()
    val channel: LiveData<Int> = Transformations.map(workingPresetDomain.workingPreset) {
        it.channel
    }
    init {
        turnToTrackMode()
    }

    fun turnToTrackMode() {
        _trackMode.value = true
        faders.addSource(trackFaders) {
            faders.value = it
            faders.removeSource(ccFaders)
        }
    }
    fun turnToCCMode() {
        _trackMode.value = false
        faders.addSource(ccFaders) {
            faders.value = it
            faders.removeSource(trackFaders)
        }
    }

    fun faderDownMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.FADER_DOWN.ordinal, 0)
    fun faderUpMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.FADER_UP.ordinal, 0)
    fun changeFaderValueMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.FADER_VALUE_CHANGED.ordinal, fader.value)
    fun trackSoloOnMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.SOLO_ON.ordinal, 0)
    fun trackSoloOffMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.SOLO_OFF.ordinal, 0)
    fun trackMuteOnMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.MUTE_ON.ordinal, 0)
    fun trackMuteOffMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.MUTE_OFF.ordinal, 0)
    fun trackRecOnMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.REC_ON.ordinal, 0)
    fun trackRecOffMessage(fader: Fader) = TrackMessage(fader.id + 1, TrackStateEnums.REC_OFF.ordinal, 0)
    fun trackBankLeftMessage() = controlMessageViewmodel.bankLeftMessage
    fun trackBankRightMessage() = controlMessageViewmodel.bankRightMessage

    fun updateCCFader(fader: Fader) {
        if (fader.id < 0 || fader.id > 8) return
        val newCCListStr = ccFaders.value!!.map {
            if (it.id == fader.id) fader.map else it.map
        }.joinToString(",")
        viewModelScope.launch {
            settingRepository.updateCCList(newCCListStr)
        }
    }
    companion object {
        const val TAG = "FaderViewModel"
        const val VPAD_SERVER_KEY = "vPadServer"
    }
}