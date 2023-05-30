package top.yudoge.vpad.adapter

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.databinding.ItemFaderBinding
import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode
import top.yudoge.vpad.toplevel.OnFaderStateChanged
import top.yudoge.vpad.toplevel.initToFader
import top.yudoge.vpad.toplevel.setOnDoubleClickListener
import top.yudoge.vpad.toplevel.showInputDialog
import top.yudoge.vpad.view.VerticalSeekBar
import top.yudoge.vpad.viewmodel.FaderViewModel
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpadapi.structure.Message

class FaderGroupAdapter(
    private val context: Context,
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
            binding.setCc.setOnClickListener {
                context.showInputDialog("Set CC", fader.map.toString(), "1~128", InputType.TYPE_CLASS_NUMBER) {
                    val int = it.toIntOrNull()
                    if (int==null) {
                        Toast.makeText(context, "请输入数字", Toast.LENGTH_SHORT).show()
                    } else if (int < 1|| int > 128) {
                        Toast.makeText(context, "请输1~128", Toast.LENGTH_SHORT).show()
                    } else {
                        faderViewModel.updateCCFader(Fader(fader.id, fader.value, fader.mode, it.toInt()))
                    }
                }
            }
            // 初始化fader
            binding.faderBar.initToFader(fader, onHasMessageToSend, faderViewModel)
        }
    }
}