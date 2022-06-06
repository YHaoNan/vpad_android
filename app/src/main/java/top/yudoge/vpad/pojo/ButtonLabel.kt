package top.yudoge.vpad.pojo

import android.graphics.Color
import top.yudoge.vpad.R

data class ButtonLabel(var label: String, var subLabel: String = "", val labelColor: Int = R.color.darkwhite, val labelColorHighlight: Int = R.color.white)
