package top.yudoge.vpad.view.setting_view

class SwitchSettingItem(
    id: Int,
    title: String,
    subTitle: String?,
    var value: Boolean
) : SettingItem(id, title, subTitle)