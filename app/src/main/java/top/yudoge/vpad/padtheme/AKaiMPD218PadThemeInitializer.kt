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
        padId: Int,
        position: Int,
        onPadEvent: (padId: Int, midiState: Int) -> Unit
    ) {
        binding as ItemPadAkaiMpd218Binding
        binding.padId = "PAD ${padId}"
        binding.pad.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.pad.setBackgroundResource(R.drawable.bg_akai_mpd_218_pressed)
                    onPadEvent(padId, MidiMessage.STATE_ON)
                }
                MotionEvent.ACTION_UP -> {
                    binding.pad.setBackgroundResource(R.drawable.bg_akai_mpd_218_normal)
                    onPadEvent(padId, MidiMessage.STATE_OFF)
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.pad.setBackgroundResource(R.drawable.bg_akai_mpd_218_normal)
                    onPadEvent(padId, MidiMessage.STATE_OFF)
                }
            }
            true
        }
    }

    companion object {
        const val TAG = "AKaiMPD218PadThemeInitializer"
    }
}