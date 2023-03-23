package top.yudoge.vpad.adapter

import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.databinding.ItemButtonKompleteBinding
import top.yudoge.vpad.pojo.ButtonLabel
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpad.viewmodel.ButtonGroupViewModel
import top.yudoge.vpad.viewmodel.PadViewModel

class ButtonGroupAdapter(
    private val padViewModel: PadViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val buttonClickListener: (label: ButtonLabel) -> Unit
) : RecyclerView.Adapter<ButtonGroupAdapter.ViewHolder>() {

    private val vm = ButtonGroupViewModel()
    private var bpm: Int? = null

    fun getBpm(): Int {
       return bpm ?: Constants.DEFAULT_BPM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemButtonKompleteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        vm.buttonGroupLabels[position]
    )

    override fun getItemCount() = vm.buttonGroupLabels.size

    inner class ViewHolder(
        private val binding: ItemButtonKompleteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(buttonLabel: ButtonLabel) {
            binding.title = buttonLabel.label
            binding.subTitle = buttonLabel.subLabel
            binding.labelColor = buttonLabel.labelColor
            binding.button.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        binding.labelColor = buttonLabel.labelColorHighlight
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        binding.labelColor = buttonLabel.labelColor
                        buttonClickListener(buttonLabel)
                    }
                }
                true
            }

            if (buttonLabel.label.equals("BPM")) {
                padViewModel.bpm.observe(lifecycleOwner) {
                    binding.subTitle = it.toString()
                    bpm = it
                }
            } else {

                binding.title = buttonLabel.label
            }
        }
    }
    companion object {
        const val TAG = "ButtonGroupAdapter"
    }
}

