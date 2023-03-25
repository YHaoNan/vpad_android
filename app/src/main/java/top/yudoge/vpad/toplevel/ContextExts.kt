package top.yudoge.vpad.toplevel

import android.app.Activity
import android.app.AlertDialog
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
import android.widget.ArrayAdapter
import android.widget.ListView


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


    AlertDialog.Builder(this)
        .setView(edit)
        .setTitle(title)
        .setPositiveButton(R.string.sure, DialogInterface.OnClickListener { dialogInterface, i ->
            inputCallback(edit.text.toString())
        })
        .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i -> }).show()

}

inline fun Context.showMessageDialog(
    title: String, message: String,
    okLabel: CharSequence = "OK",
    okCallback: DialogInterface.OnClickListener = DialogInterface.OnClickListener {di, i -> di.dismiss()},
    cancelLabel: CharSequence = "Cancel",
    cancelCallback: DialogInterface.OnClickListener = DialogInterface.OnClickListener {di, i -> di.dismiss()},
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(okLabel, okCallback)
        .setNegativeButton(cancelLabel, cancelCallback).show()
}

inline fun <T> Context.showListDialog(title: String, list: List<T>, map: (T)->String, crossinline callback: (T) -> Unit) {
    val dialog = AlertDialog.Builder(this);
    val adapter = ArrayAdapter<String>(this@showListDialog, android.R.layout.simple_list_item_1, list.map(map));
    dialog
        .setView(ListView(this))
        .setTitle(title)
        .setCancelable(false)
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
): AlertDialog {
    val dialogBuilder = AlertDialog.Builder(this)
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