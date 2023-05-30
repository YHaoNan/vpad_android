package top.yudoge.vpad.view.setting_view.layout_resolver

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import top.yudoge.vpad.R
import top.yudoge.vpad.view.setting_view.InputSettingItem
import top.yudoge.vpad.view.setting_view.SettingItem
import top.yudoge.vpad.view.setting_view.SettingView

open class InputSettingLayoutResolver : SettingLayoutResolver {

    override fun layoutResId() = R.layout.item_input_setting

    override fun bindView(
        settingBody: ViewGroup,
        settingItem: SettingItem,
        onSettingItemChangedListener: SettingView.OnSettingItemChangedListener?
    ) {

        settingItem as InputSettingItem

        val preview = settingBody.findViewById<TextView>(R.id.setting_preview)
        preview.text = settingItem.value

        settingBody.setOnClickListener {
            val context = settingBody.context

            val edit = EditText(context).apply {
                setText(settingItem.value)
                setHint(settingItem.hint)
                inputType = settingItem.inputType
                selectAll()
            }

            AlertDialog.Builder(context)
                .setView(edit)
                .setTitle(settingItem.title)
                .setPositiveButton(R.string.sure, DialogInterface.OnClickListener { dialogInterface, i ->
                    if (settingItem.value == edit.text.toString()) return@OnClickListener
                    if (!settingItem.validFn(edit.text.toString())) {
                        Toast.makeText(context, settingItem.errorMessage, Toast.LENGTH_SHORT).show()
                        return@OnClickListener
                    }
                    settingItem.value = edit.text.toString()
                    preview.text = settingItem.value
                    onSettingItemChangedListener?.onChanged(settingItem)
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i -> }).show()
        }
    }
}