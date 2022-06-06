package top.yudoge.vpad.view.setting_view.layout_resolver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView

class NavigationSettingLayoutResolver : SettingLayoutResolver {

    override fun layoutResId() = R.layout.item_navigation_setting

    override fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    ) {
        settingBody.setOnClickListener {
            // NavigationSetting主要用于导航到另一个页面，所以它里面没有修改设置值的逻辑，只是简单通知外界它被点击了
            onSettingItemChangedListener?.onChanged(settingItem)
        }
    }

}