package top.yudoge.vpad.padtheme

import android.annotation.SuppressLint
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import top.yudoge.vpad.R
import top.yudoge.vpad.databinding.ItemPadAkaiMpd218Binding
import top.yudoge.vpad.pojo.PadSetting
import top.yudoge.vpad.toplevel.PadEvent
import top.yudoge.vpadapi.structure.MidiMessage

@SuppressLint("LongLogTag")
class AKaiMPD218PadThemeInitializer : PadThemeInitializer() {

    override fun getView(parent: ViewGroup, viewType: Int): ViewDataBinding {
         return ItemPadAkaiMpd218Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindView(
        binding: ViewDataBinding,
        padSetting: PadSetting,
        padPosition: Int,
        settingMode: Boolean,
        onPadEvent: (padSetting: PadSetting, padIndex: Int, event: PadEvent) -> Unit
    ) {
        binding as ItemPadAkaiMpd218Binding
        binding.padTitle = padSetting.title
        binding.deleteVisiable = settingMode
        binding.pad.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    turnOnPadViewManually(binding.pad)
                    if(!settingMode) onPadEvent(padSetting, padPosition, PadEvent.Down)
                }
                MotionEvent.ACTION_UP -> {
                    turnOffPadViewManually(binding.pad)
                    if (!settingMode) onPadEvent(padSetting, padPosition, PadEvent.Release)
                    else onPadEvent(padSetting, padPosition, PadEvent.OpenSetting)
                }
                MotionEvent.ACTION_CANCEL -> {
                    turnOffPadViewManually(binding.pad)
                    if (!settingMode) onPadEvent(padSetting, padPosition, PadEvent.Release)
                    else onPadEvent(padSetting, padPosition, PadEvent.OpenSetting)
                }
            }
            true
        }
        binding.deleteBtn.setOnClickListener {
            onPadEvent(padSetting, padPosition, PadEvent.Delete)
        }
    }

    override fun turnOnPadViewManually(padView: View) {
        padView.setBackgroundResource(R.drawable.bg_akai_mpd_218_pressed)
    }

    override fun turnOffPadViewManually(padView: View) {
        padView.setBackgroundResource(R.drawable.bg_akai_mpd_218_normal)
    }

    companion object {
        const val TAG = "AKaiMPD218PadThemeInitializer"
    }
}