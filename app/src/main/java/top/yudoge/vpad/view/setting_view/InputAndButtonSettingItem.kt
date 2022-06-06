package top.yudoge.vpad.view.setting_view

class InputAndButtonSettingItem(
    id: Int,
    title: String,
    subTitle: String?,
    value: String,
    hint: String = "",
    inputType: Int,
    val step1: Int = 1,
    val step2: Int = 10,
    val step2Show: Boolean = true
) : InputSettingItem(id, title, subTitle, value, hint, inputType)
