package top.yudoge.vpad.padtheme

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import top.yudoge.vpad.pojo.PadSetting

/**
 * 代表一个Pad布局初始化器
 */
abstract class PadThemeInitializer() {
    abstract fun getView(parent: ViewGroup, viewType: Int): ViewDataBinding
    abstract fun bindView(
        databinding: ViewDataBinding,
        padSetting: PadSetting,
        padPosition: Int,
        onPadEvent: (padSetting: PadSetting, padIndex: Int, state: Int) -> Unit
    )

    abstract fun turnOnPadViewManually(padView: View);
    abstract fun turnOffPadViewManually(padView: View);

}
