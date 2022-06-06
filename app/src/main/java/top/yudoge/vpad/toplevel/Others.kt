package top.yudoge.vpad.toplevel

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode
import top.yudoge.vpad.view.VerticalSeekBar
import top.yudoge.vpad.view.WheelStateChangeListener
import top.yudoge.vpad.viewmodel.FaderViewModel
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpadapi.structure.CCMessage
import top.yudoge.vpadapi.structure.Message


fun SeekBar.setOnWheelStateChangeListener(wheelStateChangeListener: WheelStateChangeListener) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            wheelStateChangeListener.onValueChanged(p0, p1)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            wheelStateChangeListener.onFingerOut(p0)
        }

    })
}

fun <K,V> Map<K,V>.myGetOrDefault(key: K, default: V) : V = if (containsKey(key)) get(key)!! else default

/**
 * 当设置了DoubleClickListener时，OnClickListener会失效
 */
fun View.setOnDoubleClickListener(timeInterval: Int = 140, onDoubleClickListener: View.OnClickListener) {

    var prevClickTime = Long.MIN_VALUE
    setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            if (now - prevClickTime < timeInterval) onDoubleClickListener.onClick(this)
            prevClickTime = now
        }
        false
    }
}

fun VerticalSeekBar.initToFader(fader: Fader, onHasAMessageToSend: (Message) -> Unit, faderViewModel: FaderViewModel) {
    // 设置最大值和初始值
    max = 127
    setProgressAndThumb(97)

    val faderStateChanged = OnFaderStateChanged(fader, onHasAMessageToSend, faderViewModel)

    setOnSeekBarChangeListener(faderStateChanged)
}

class OnFaderStateChanged(private val fader: Fader, private val onHasAMessageToSend: (Message) -> Unit, private val faderViewModel: FaderViewModel) : SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        if (progress > 127 || progress < 0) return
        Log.i("Fader value changed", progress.toString())
        if (fader.mode == FaderMode.Track) {
            // Change Track Fader
            onHasAMessageToSend(faderViewModel.changeFaderValueMessage(fader.copy(value = progress)))
        } else {
            // Send CC Message
            onHasAMessageToSend(CCMessage(fader.map, progress))
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (fader.mode == FaderMode.Track) {
            onHasAMessageToSend(faderViewModel.faderDownMessage(fader))
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (fader.mode == FaderMode.Track) {
            onHasAMessageToSend(faderViewModel.faderUpMessage(fader))
        }
    }

}