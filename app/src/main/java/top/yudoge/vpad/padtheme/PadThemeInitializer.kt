package top.yudoge.vpad.padtheme

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import top.yudoge.vpad.pojo.PadSetting
import top.yudoge.vpad.toplevel.MidiNoteName
import top.yudoge.vpad.toplevel.PadEvent

/**
 * 代表一个Pad布局初始化器
 */
abstract class PadThemeInitializer() {

    abstract fun getView(parent: ViewGroup, viewType: Int): ViewDataBinding
    abstract fun bindView(
        databinding: ViewDataBinding,
        padSetting: PadSetting,
        padPosition: Int,
        padNote: Int,
        settingMode: Boolean,
        showNoteName: Boolean,
        eventEmitter: (String) -> Unit
    )

    abstract fun turnOnPadViewManually(padView: View);
    abstract fun turnOffPadViewManually(padView: View);

    companion object {
        val DELETE = "DELETE"
    }
}
