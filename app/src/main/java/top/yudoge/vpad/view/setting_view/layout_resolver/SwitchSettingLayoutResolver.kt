package top.yudoge.vpad.view.setting_view.layout_resolver

import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView
import top.yudoge.vpad.view.setting_view.SwitchSettingItem

class SwitchSettingLayoutResolver : SettingLayoutResolver {

    override fun layoutResId() = R.layout.item_switch_setting

    override fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    ) {
        settingItem as SwitchSettingItem

        val switch = settingBody.findViewById<Switch>(R.id.setting_preview).apply {
            isChecked = settingItem.value
            setOnCheckedChangeListener { compoundButton, b ->
                settingItem.value = b
                onSettingItemChangedListener?.onChanged(settingItem)
            }
        }

        settingBody.setOnClickListener {
            switch.performClick()
        }

    }
}