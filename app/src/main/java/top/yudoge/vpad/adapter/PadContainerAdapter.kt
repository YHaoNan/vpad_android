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
    private var baseNote: Int,
    private var showNoteName: Int,
    private val onPadEvent: (padSetting: PadSetting, padIndex: Int, event: PadEvent) -> Unit
) : RecyclerView.Adapter<PadContainerAdapter.ViewHolder?>() {
    private var padControllers: MutableList<PadTouchController?> = mutableListOf(*arrayOfNulls(padSettings.size))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(padThemeInitializer.getView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val setting = padSettings[position]
        // 调用PadThemeInitializer bindView
        padThemeInitializer.bindView(holder.dataBinding, setting, position, baseNote + setting.offset, settingMode, showNoteName == 1) {event ->
            when (event) {
                PadThemeInitializer.DELETE -> onPadEvent(setting, position, PadEvent.Delete)
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

    fun setShowNoteName(showNoteName: Int) {
        if (showNoteName == this.showNoteName) return
        this.showNoteName = showNoteName
        this.notifyDataSetChanged()
    }

    class ViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        lateinit var touchController: PadTouchController
        fun bindTouchController(controller: PadTouchController) {
            dataBinding.root.setOnTouchListener(controller)
            touchController = controller
        }
    }

}