package top.yudoge.vpad.toplevel

import android.content.Context
import android.os.Environment
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
import java.io.File
import java.net.NetworkInterface


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

fun SeekBar.initToFader(channel: Int, fader: Fader, onHasAMessageToSend: (Message) -> Unit, faderViewModel: FaderViewModel) {
    // 设置最大值和初始值
    max = 127
    setProgress(fader.value)

    val faderStateChanged = OnFaderStateChanged(channel, fader, onHasAMessageToSend, faderViewModel)

    setOnSeekBarChangeListener(faderStateChanged)
}

class OnFaderStateChanged(private val channel: Int, private val fader: Fader, private val onHasAMessageToSend: (Message) -> Unit, private val faderViewModel: FaderViewModel) : SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        if (progress > 127 || progress < 0) return
        Log.d("Fader value changed", progress.toString())
        if (fader.mode == FaderMode.Track) {
            // Change Track Fader
            onHasAMessageToSend(faderViewModel.changeFaderValueMessage(fader.copy(value = progress)))
        } else {
            // Send CC Message
            onHasAMessageToSend(CCMessage(fader.map, progress, channel))
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

/**
 * 获取代表Preset目录的File，该File是一个Dir
 * 调用该方法时preset目录可能不存在，该方法必须自动创建该目录
 * 也可能有和preset同名的文件阻挡创建preset目录，此时该方法要删除这个文件再创建目录
 * 该方法一定在文件读写权限被正确授予时才能调用
 */
fun safeGetPresetDir(context: Context): File {
    val presetDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), Constants.PRESET_DIR)

    if (presetDir.isFile()) presetDir.delete()
    if (!presetDir.exists()) presetDir.mkdirs()

    return presetDir
}

fun NetworkInterface.getIpv4AddressNullable() = this.inetAddresses.toList().find { it.address.size == 4 }
/**
 * crash when interface has no ipv4 address
 */
fun NetworkInterface.getIpv4Address() = this.inetAddresses.toList().find { it.address.size == 4 }!!
fun NetworkInterface.getLabel() = "${this.displayName}@${this.getIpv4Address()}"

fun List<NetworkInterface>.allIpv4AndNotLoopbackIfaces() = filter { !it.isLoopback && it.getIpv4AddressNullable() != null }

