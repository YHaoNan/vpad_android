package top.yudoge.vpad.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.databinding.ItemFaderBinding
import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode
import top.yudoge.vpad.toplevel.OnFaderStateChanged
import top.yudoge.vpad.toplevel.initToFader
import top.yudoge.vpad.toplevel.setOnDoubleClickListener
import top.yudoge.vpad.view.VerticalSeekBar
import top.yudoge.vpad.viewmodel.FaderViewModel
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpadapi.structure.Message

class FaderGroupAdapter(
    private val onHasMessageToSend: (Message) -> Unit,
    private val faderViewModel: FaderViewModel
) : RecyclerView.Adapter<FaderGroupAdapter.ViewHolder>() {

    private var faders: List<Fader> = listOf()

    fun setFaders(faders: List<Fader>) {
        this.faders = faders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(faders[position])
    }

    override fun getItemCount(): Int {
        return faders.size
    }

    private fun Message.notifyUIToSend() = onHasMessageToSend(this)
    inner class ViewHolder(private val binding: ItemFaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fader: Fader) {
            binding.fader = fader
            binding.tag.text = "Track ${fader.map}"
            binding.solo.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) faderViewModel.trackSoloOnMessage(fader).notifyUIToSend()
                else faderViewModel.trackSoloOffMessage(fader).notifyUIToSend()
            }
            binding.mute.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) faderViewModel.trackMuteOnMessage(fader).notifyUIToSend()
                else faderViewModel.trackMuteOffMessage(fader).notifyUIToSend()
            }
            binding.record.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) faderViewModel.trackRecOnMessage(fader).notifyUIToSend()
                else faderViewModel.trackRecOffMessage(fader).notifyUIToSend()
            }
            // 初始化fader
            binding.faderBar.initToFader(fader, onHasMessageToSend, faderViewModel)
        }
    }
}