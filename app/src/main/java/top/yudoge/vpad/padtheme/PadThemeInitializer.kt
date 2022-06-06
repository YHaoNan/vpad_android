package top.yudoge.vpad.padtheme

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

/**
 * 代表一个Pad布局初始化器
 */
abstract class PadThemeInitializer() {
    abstract fun getView(parent: ViewGroup, viewType: Int): ViewDataBinding
    abstract fun bindView(
        databinding: ViewDataBinding,
        padId: Int,
        position: Int,
        onPadEvent: (padId: Int, midiState: Int) -> Unit
    )

}
