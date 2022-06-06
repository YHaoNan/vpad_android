package top.yudoge.vpad.adapter.binding

import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter

private const val TAG = "ViewBindingAdapter"

@BindingAdapter("visiableOrGone")
fun View.visiableOrGone(visiable: Boolean) {
    visibility = if (visiable) View.VISIBLE else View.GONE
}