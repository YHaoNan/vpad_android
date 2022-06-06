package top.yudoge.vpad.adapter.binding

import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("textColorResource")
fun TextView.textColorResource(colorResId: Int) {
    setTextColor(context.resources.getColor(colorResId))
}

@BindingAdapter("assetsFont")
fun TextView.assetsFont(path: String) {
    setTypeface(Typeface.createFromAsset(context.assets, path))
}