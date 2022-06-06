package top.yudoge.vpad.view.setting_view.layout_resolver

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginRight
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.SelectSettingItem
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView
import java.lang.IllegalStateException

class SelectSettingLayoutResolver : SettingLayoutResolver {

    override fun layoutResId() = R.layout.item_select_setting

    override fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    ) {

        settingItem as SelectSettingItem

        val spinner = settingBody.findViewById<Spinner>(R.id.setting_preview).apply {
            setPadding(20, paddingTop, 20, paddingBottom)
            adapter = ArrayAdapter(
                settingBody.context,
                android.R.layout.simple_list_item_1,
                settingItem.values
            )
            setSelection(settingItem.valueId)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (settingItem.valueId == p2)return
                    settingItem.valueId = p2
                    onSettingItemChangedListener?.onChanged(settingItem)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    throw IllegalStateException()
                }
            }
        }

        settingBody.setOnClickListener {
            // show spinner...
            spinner.performClick()
        }

    }
}