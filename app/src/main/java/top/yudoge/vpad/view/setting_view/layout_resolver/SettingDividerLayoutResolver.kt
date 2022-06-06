package top.yudoge.vpad.view.setting_view.layout_resolver

import android.view.View
import android.view.ViewGroup
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView

class SettingDividerLayoutResolver : SettingLayoutResolver {
    override fun layoutResId() = R.layout.item_setting_divider

    override fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    ) {}

}