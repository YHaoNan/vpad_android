package top.yudoge.vpad.view.setting_view

class SelectSettingItem(
    id: Int,
    title: String,
    subTitle:String?,
    var valueId: Int,
    val values: List<String>
) : SettingItem(id, title, subTitle)