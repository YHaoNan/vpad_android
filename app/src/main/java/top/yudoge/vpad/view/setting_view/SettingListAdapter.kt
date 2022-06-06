package top.yudoge.vpad.view.setting_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.layout_resolver.*
import java.lang.IllegalArgumentException

class SettingListAdapter(
    private val settingList: List<SettingItem>,
    private val onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
) : RecyclerView.Adapter<SettingListAdapter.ViewHolder>() {

    private val layoutResolvers: Array<SettingLayoutResolver> = arrayOf(
        NavigationSettingLayoutResolver(), InputSettingLayoutResolver(), SwitchSettingLayoutResolver(),
        SelectSettingLayoutResolver(), SettingDividerLayoutResolver(), InputAndButtonSettingLayoutResolver()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    layoutResolvers[viewType].layoutResId(),
                    parent,false
                )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(position)

    override fun getItemViewType(position: Int) =
        when(settingList[position]) {
            is NavigationSettingItem -> TYPE_NAVIGATION
            // 必须放在InputSettingItem上面，因为二者有父子关系
            is InputAndButtonSettingItem -> TYPE_INPUT_AND_BUTTON
            is InputSettingItem -> TYPE_INPUT
            is SwitchSettingItem -> TYPE_SWITCH
            is SelectSettingItem -> TYPE_SELECT
            is SettingDivider -> TYPE_DIVIDER
            else -> throw IllegalArgumentException("Unknown implemention of `SettingItem` -> ${settingList[position]::class.qualifiedName}.")
        }

    override fun getItemCount() =
        settingList.size

    private companion object {
        const val TYPE_NAVIGATION = 0
        const val TYPE_INPUT = 1
        const val TYPE_SWITCH = 2
        const val TYPE_SELECT = 3
        const val TYPE_DIVIDER = 4
        const val TYPE_INPUT_AND_BUTTON = 5
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val settingItem = settingList[position]
            val resolver = layoutResolvers[getItemViewType(position)]

            // 绑定公共资源和事件
            val settingBody = itemView.findViewById<ViewGroup>(R.id.setting_body)

            itemView.findViewById<TextView>(R.id.setting_title).apply {
                text = settingItem.title
            }
            itemView.findViewById<TextView>(R.id.setting_sub_title).apply {
                if (settingItem.subTitle == null) {
                    visibility = View.GONE
                } else {
                    text = settingItem.subTitle
                }
            }

            // 调用layoutResolver绑定不同类型item的特有资源和事件
            resolver.bindView(settingBody, settingItem, onSettingItemChangedListener)

        }
    }

}