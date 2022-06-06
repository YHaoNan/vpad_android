package top.yudoge.vpad.view.setting_view.layout_resolver

import android.view.View
import android.view.ViewGroup
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView

interface SettingLayoutResolver {
    fun layoutResId(): Int
    fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    )
}