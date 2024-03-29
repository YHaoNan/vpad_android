package top.yudoge.vpad.view.setting_view.layout_resolver

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.InputAndButtonSettingItem
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView

class InputAndButtonSettingLayoutResolver : InputSettingLayoutResolver() {

    override fun layoutResId() = R.layout.item_input_with_button_setting

    override fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    ) {
        settingItem as InputAndButtonSettingItem
        super.bindView(settingBody, settingItem, onSettingItemChangedListener)
        val preview = settingBody.findViewById<TextView>(R.id.setting_preview)
        settingBody.findViewById<TextView>(R.id.setting_neg_step_1).apply {
            setOnClickListener {
                val value = settingItem.value.toInt()
                val newValue = value - settingItem.step1;
                if (!settingItem.validFunc(newValue)) {
                    Toast.makeText(context, settingItem.errorMessage, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                settingItem.value = newValue.toString()
                onSettingItemChangedListener?.onChanged(settingItem)
            }
        }
        settingBody.findViewById<TextView>(R.id.setting_posi_step_1).apply {
            setOnClickListener {
                val value = settingItem.value.toInt()
                val newValue = value + settingItem.step1;
                if (!settingItem.validFunc(newValue)) {
                    Toast.makeText(context, settingItem.errorMessage, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                settingItem.value = newValue.toString()
                onSettingItemChangedListener?.onChanged(settingItem)
            }
        }

        settingBody.findViewById<TextView>(R.id.setting_neg_step_2).apply {
            if (!settingItem.step2Show) {
                visibility = View.GONE
                return@apply
            }
            setOnClickListener {
                val value = settingItem.value.toInt()
                val newValue = value - settingItem.step2;
                if (!settingItem.validFunc(newValue)) {
                    Toast.makeText(context, settingItem.errorMessage, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                settingItem.value = newValue.toString();
                onSettingItemChangedListener?.onChanged(settingItem)
            }
        }
        settingBody.findViewById<TextView>(R.id.setting_posi_step_2).apply {
            if (!settingItem.step2Show) {
                visibility = View.GONE
                return@apply
            }
            setOnClickListener {
                val value = settingItem.value.toInt()
                val newValue = value + settingItem.step2;
                if (!settingItem.validFunc(newValue)) {
                    Toast.makeText(context, settingItem.errorMessage, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                settingItem.value = newValue.toString();
                onSettingItemChangedListener?.onChanged(settingItem)
            }
        }



    }
}