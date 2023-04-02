package top.yudoge.vpad.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.databinding.ItemPadAkaiMpd218Binding
import top.yudoge.vpad.pojo.PadSetting
import top.yudoge.vpad.padtheme.PadThemeInitializer
import top.yudoge.vpad.toplevel.PadEvent

class PadContainerAdapter(
    private val padThemeInitializer: PadThemeInitializer,
    private val padSettings: List<PadSetting>,
    private var settingMode: Boolean,
    private val onPadEvent: (padSetting: PadSetting, padIndex: Int, event: PadEvent) -> Unit
) : RecyclerView.Adapter<PadContainerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(padThemeInitializer.getView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        padThemeInitializer.bindView(holder.dataBinding, padSettings[position], position, settingMode, onPadEvent)
    }

    override fun getItemCount() = padSettings.size;

    fun enableSettingMode() {
        settingMode = true
        notifyDataSetChanged()
    }

    fun disableSettingMode() {
        settingMode = false
        notifyDataSetChanged()
    }

    class ViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

}