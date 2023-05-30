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
    val step2Show: Boolean = true,
    errorMsg: String,
    val validFunc: (Int)->Boolean
) : InputSettingItem(id, title, subTitle, value, hint, inputType, errorMsg, {
    val num = it.toIntOrNull();
    if (num==null) false
    else validFunc(num)
})
