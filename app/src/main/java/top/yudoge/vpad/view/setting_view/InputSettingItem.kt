package top.yudoge.vpad.view.setting_view

import android.text.InputType

open class InputSettingItem(
    id: Int,
    title: String,
    subTitle: String?,
    var value: String,
    val hint: String = "",
    val inputType: Int,
    val errorMessage: String,
    val validFn: (String) -> Boolean
) : SettingItem(id, title, subTitle)