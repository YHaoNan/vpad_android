package top.yudoge.vpad.viewmodel

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.CommunicatorHolder
import top.yudoge.vpad.api.TrackStateEnums
import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.Message
import top.yudoge.vpadapi.structure.TrackMessage
import javax.inject.Inject


@HiltViewModel
class FaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val controlMessageViewmodel: ControlMessageViewmodel
): ViewModel() {
    private val vPadServer = savedStateHandle.get<VPadServer>(VPAD_SERVER_KEY)!!

    private val _trackMode = MutableLiveData(true)
    val trackMode by ::_trackMode

    private val _faders = MutableLiveData(Constants.DEFAULT_TRACK_FADERS.value)
    val faders: LiveData<List<Fader>> by ::_faders

    fun turnToTrackMode() {
        _trackMode.value = true
        _faders.value = Constants.DEFAULT_TRACK_FADERS.value
    }
    fun turnToCCMode() {
        _trackMode.value = false
        _faders.value = Constants.DEFAULT_CC_FADERS.value
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


    companion object {
        const val TAG = "FaderViewModel"
        const val VPAD_SERVER_KEY = "vPadServer"
    }
}