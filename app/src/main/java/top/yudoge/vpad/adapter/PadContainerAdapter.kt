package top.yudoge.vpad.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.pojo.PadSetting
import top.yudoge.vpad.padtheme.PadThemeInitializer

class PadContainerAdapter(
    private val padThemeInitializer: PadThemeInitializer,
    private val padSettings: List<PadSetting>,
    private val onPadEvent: (padSetting: PadSetting, padIndex: Int, state: Int) -> Unit
) : RecyclerView.Adapter<PadContainerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(padThemeInitializer.getView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        padThemeInitializer.bindView(holder.dataBinding, padSettings[position], position, onPadEvent)
    }

    override fun getItemCount() = padSettings.size;

    class ViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}