package top.yudoge.vpad.ui

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.databinding.ViewDataBinding
import top.yudoge.vpad.padtheme.PadThemeInitializer
import top.yudoge.vpad.pojo.PadSetting
import top.yudoge.vpad.toplevel.PadEvent
import top.yudoge.vpad.toplevel.TriggerMode

class PadTouchController(
    private val padSetting: PadSetting,
    private val padPosition: Int,
    private val settingMode: Boolean,
    private val onPadEvent: (padSetting: PadSetting, padIndex: Int, event: PadEvent) -> Unit,
    private val rootView: View,
    private val padThemeInitializer: PadThemeInitializer
) : OnTouchListener {

    private var padActive = false

    private fun paddown() {
        if (padActive) return
        onPadEvent(padSetting, padPosition, PadEvent.Down)
        padActive = true
        padThemeInitializer.turnOnPadViewManually(rootView)
    }

    private fun padrelease() {
        if (!padActive) return
        onPadEvent(padSetting, padPosition, PadEvent.Release)
        padActive = false
        padThemeInitializer.turnOffPadViewManually(rootView)
    }

    fun onViewDestroy() {
        padrelease()
    }

    override fun onTouch(v: View, motionEvent: MotionEvent): Boolean {
        when(motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                if (settingMode) {
                    padThemeInitializer.turnOnPadViewManually(rootView)
                } else if (padSetting.triggerMode == TriggerMode.Hold) {
                    if (padActive) padrelease() else paddown()
                } else if (padSetting.triggerMode == TriggerMode.Trigger) {
                    paddown()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (settingMode) {
                    padThemeInitializer.turnOffPadViewManually(rootView)
                    onPadEvent(padSetting, padPosition, PadEvent.OpenSetting)
                } else if (padSetting.triggerMode == TriggerMode.Hold) {
                } else if (padSetting.triggerMode == TriggerMode.Trigger) {
                    padrelease()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (settingMode) {
                    padThemeInitializer.turnOffPadViewManually(rootView)
                    onPadEvent(padSetting, padPosition, PadEvent.OpenSetting)
                } else if (padSetting.triggerMode == TriggerMode.Hold) {
                } else if (padSetting.triggerMode == TriggerMode.Trigger) {
                    padrelease()
                }
            }
        }
        return true
    }
}