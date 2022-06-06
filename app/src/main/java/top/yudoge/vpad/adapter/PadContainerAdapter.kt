package top.yudoge.vpad.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.padtheme.PadThemeInitializer

class PadContainerAdapter(
    private val padThemeInitializer: PadThemeInitializer,
    private val onPadEvent: (padId: Int, state: Int) -> Unit
) : RecyclerView.Adapter<PadContainerAdapter.ViewHolder>() {

    val pads = listOf(
        13,14,15,16,
        9,10,11,12,
        5,6,7,8,
        1,2,3,4
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(padThemeInitializer.getView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        padThemeInitializer.bindView(holder.dataBinding, pads[position], position, onPadEvent)
    }

    override fun getItemCount() = pads.size

    class ViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}