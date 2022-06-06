package top.yudoge.vpad.view.setting_view

import android.content.AttributionSource
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingView(
    context: Context,
    attributeSet: AttributeSet,
) : RecyclerView(context, attributeSet) {

    fun interface OnSettingItemChangedListener {
        fun onChanged(settingItem: SettingItem)
    }

    // set before setSettingItem
    var settingItemChangedListener: OnSettingItemChangedListener? = null

    private var settingItems: List<SettingItem>? = null

    fun setSettingItem(settingItems: List<SettingItem>) {
        this.settingItems = settingItems
        this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SettingListAdapter(settingItems, settingItemChangedListener)
    }


}