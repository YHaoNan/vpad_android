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
import top.yudoge.vpad.toplevel.MidiNoteName
import top.yudoge.vpad.toplevel.PadEvent
import top.yudoge.vpad.toplevel.TriggerMode
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

    private fun getNameByMidiNoteName(mnn: MidiNoteName?): String {
        if (mnn == null) return "N/A";
        if (mnn.nameInFlat.equals(mnn.nameInSharp)) {
            return mnn.nameInSharp;
        } else {
            return mnn.nameInSharp + "/" + mnn.nameInFlat;
        }
    }
    override fun bindView(
        binding: ViewDataBinding,
        padSetting: PadSetting,
        padPosition: Int,
        padNote: Int,
        settingMode: Boolean,
        showNoteName: Boolean,
        eventEmitter: (String) -> Unit
    ) {
        binding as ItemPadAkaiMpd218Binding
        binding.padTitle = if (showNoteName) getNameByMidiNoteName(MidiNoteName.byNote(padNote)) else padSetting.title
        binding.deleteVisiable = settingMode
        binding.deleteBtn.setOnClickListener {  eventEmitter(PadThemeInitializer.DELETE)}
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