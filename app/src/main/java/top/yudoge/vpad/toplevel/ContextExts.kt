package top.yudoge.vpad.toplevel

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Environment
import android.text.InputType
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import top.yudoge.vpad.R
import java.io.File

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Modifier


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


inline fun Context.showInputDialog(title: String, value: String?, hint: String?, inputType: Int = InputType.TYPE_CLASS_NUMBER, crossinline inputCallback: (text: String) -> Unit) {

    val edit = EditText(this).apply {
        value?.let {
            setText(it)
        }
        hint?.let {
            setHint(it)
        }
    }


    android.app.AlertDialog.Builder(this)
        .setView(edit)
        .setTitle(title)
        .setPositiveButton(R.string.sure, DialogInterface.OnClickListener { dialogInterface, i ->
            inputCallback(edit.text.toString())
        })
        .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i -> }).show()

}

data class InputEntry (val key: String, val value: String, val label: String, val keyboardType: KeyboardType)

inline fun ViewGroup.attachMultipleInputDialog(title: String, inputs: List<InputEntry>, crossinline inputCallback: (values: Map<String, String>) -> Unit) {
    val container = ComposeView(context)
    val map = mutableMapOf<String, String>()
    inputs.forEach {
        map.put(it.key, it.value)
    }
    container.setContent {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = title) },
            text = {
                Column {
                    inputs.forEach { entry ->
                        var text by remember { mutableStateOf(TextFieldValue(entry.value)) }
                        TextField(
                            value = text,
                            label = { Text(text = entry.label) },
                            keyboardOptions = KeyboardOptions(keyboardType = entry.keyboardType),
                            onValueChange = {
                                text = it
                                map.put(entry.key, it.text)
                            },
                            enabled = true,
                            readOnly = false
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {inputCallback.invoke(map); removeView(container)}) { Text(text = "OK") }
            },
            dismissButton = {
                TextButton(onClick = {removeView(container)}) { Text(text = "Cancel") }
            }
        )
    }
    addView(container)
}

inline fun Fragment.rootView(binding: ViewBinding) = binding.root as ViewGroup
inline fun Activity.rootView(binding: ViewBinding) = binding.root as ViewGroup

inline fun Context.showMessageDialog(
    title: String, message: String,
    okLabel: CharSequence = "OK",
    okCallback: DialogInterface.OnClickListener = DialogInterface.OnClickListener {di, i -> di.dismiss()},
    cancelLabel: CharSequence = "Cancel",
    cancelCallback: DialogInterface.OnClickListener = DialogInterface.OnClickListener {di, i -> di.dismiss()},
) {
    android.app.AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(okLabel, okCallback)
        .setNegativeButton(cancelLabel, cancelCallback).show()
}

inline fun <T> Context.showListDialog(title: String, list: List<T>, map: (T)->String, crossinline callback: (T) -> Unit) {
    val dialog = android.app.AlertDialog.Builder(this);
    val adapter = ArrayAdapter<String>(this@showListDialog, android.R.layout.simple_list_item_1, list.map(map));
    dialog
        .setView(ListView(this))
        .setTitle(title)
//        .setCancelable(false)
        .setSingleChoiceItems(adapter, 0) {dialog, which ->
            callback(list.get(which))
            dialog.dismiss();
        }
    dialog.show()
}

fun Context.alert(
    title: String, message: String,
    posiButtonLabel: String = "确定",
    posiButtonCallback: (dialog: DialogInterface, Int) -> Unit = { dialog, which ->
        dialog.dismiss()
    },
    negButtonLabel: String? = null , negButtonCallback: ((DialogInterface, Int) -> Unit)? = null,
    cancelable: Boolean = true
): android.app.AlertDialog {
    val dialogBuilder = android.app.AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(cancelable)
        .setPositiveButton(posiButtonLabel, posiButtonCallback)

    negButtonLabel?.let {
        dialogBuilder.setNegativeButton(negButtonLabel, negButtonCallback)
    }
    return dialogBuilder.create();
}

fun Context.hasPermission(perm: String): Boolean {
    return ContextCompat.checkSelfPermission(applicationContext, perm) == PackageManager.PERMISSION_GRANTED
}

fun Activity.checkAndRequestPermissions(perms: List<String>, requestCode: List<Int>) {
    perms.forEachIndexed { i, perm ->
        // 如果未授权
        if (ContextCompat.checkSelfPermission(applicationContext, perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm), requestCode[i])
        }
    }
}

fun Context.getCrashLog(): File? {
    val dir: File? = getExternalFilesDir(Environment.DIRECTORY_ALARMS)
    if (dir == null) return null

    val crashLogFile = File(dir, Constants.CRASH_LOG_NAME)
    return crashLogFile
}

fun Activity.share(uri: Uri, mimeType: String = "text/plain") {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    shareIntent.type = mimeType
    startActivity(shareIntent)
}