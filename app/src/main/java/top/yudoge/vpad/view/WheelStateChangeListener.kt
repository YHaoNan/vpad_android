package top.yudoge.vpad.view

import android.widget.SeekBar
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpad.viewmodel.PadViewModel
import top.yudoge.vpadapi.structure.PitchWheelMessage

interface WheelStateChangeListener {
    fun onValueChanged(seekBar: SeekBar?, value: Int)
    fun onFingerOut(seekBar: SeekBar?)
}

class PitchWheelWheelStateChangeListener(private val vm: MainViewModel) : WheelStateChangeListener {
    companion object {
        private const val MID_POS = 64
    }
    private var prevPos: Int = MID_POS
    override fun onValueChanged(seekBar: SeekBar?, value: Int) {
        vm.sendMessageToServer(PitchWheelMessage(value, prevPos))
        prevPos = value
    }

    override fun onFingerOut(seekBar: SeekBar?) {
        seekBar?.setProgress(MID_POS)
    }

}

