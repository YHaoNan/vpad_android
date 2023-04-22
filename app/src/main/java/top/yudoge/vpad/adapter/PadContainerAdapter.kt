package top.yudoge.vpad.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.databinding.ItemPadAkaiMpd218Binding
import top.yudoge.vpad.pojo.PadSetting
import top.yudoge.vpad.padtheme.PadThemeInitializer
import top.yudoge.vpad.toplevel.PadEvent
import top.yudoge.vpad.ui.PadTouchController

class PadContainerAdapter(
    private val padThemeInitializer: PadThemeInitializer,
    private val padSettings: List<PadSetting>,
    private var settingMode: Boolean,
    private val onPadEvent: (padSetting: PadSetting, padIndex: Int, event: PadEvent) -> Unit
) : RecyclerView.Adapter<PadContainerAdapter.ViewHolder?>() {
    private var padControllers: MutableList<PadTouchController?> = mutableListOf(*arrayOfNulls(padSettings.size))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(padThemeInitializer.getView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 调用PadThemeInitializer bindView
        padThemeInitializer.bindView(holder.dataBinding, padSettings[position], position, settingMode) {event ->
            when (event) {
                PadThemeInitializer.DELETE -> onPadEvent(padSettings[position], position, PadEvent.Delete)
            }
        }
        // 设置PadController
        PadTouchController(padSettings[position], position, settingMode, onPadEvent, holder.dataBinding.root, padThemeInitializer).let {
            holder.bindTouchController(it)
            padControllers[position] = it
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.touchController.onViewDestroy()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        deactiveAllPad()
        super.onDetachedFromRecyclerView(recyclerView)
    }
    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.touchController.onViewDestroy()
        super.onViewDetachedFromWindow(holder)
    }

    fun deactiveAllPad() {
        padControllers.forEach {
            it?.onViewDestroy()
        }
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

    class ViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        lateinit var touchController: PadTouchController
        fun bindTouchController(controller: PadTouchController) {
            dataBinding.root.setOnTouchListener(controller)
            touchController = controller
        }
    }

}