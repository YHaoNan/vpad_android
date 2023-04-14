package top.yudoge.vpad.ui

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import top.yudoge.vpad.BuildConfig
import top.yudoge.vpad.R
import top.yudoge.vpad.UncaughtExceptionHandler
import top.yudoge.vpad.toplevel.*
import top.yudoge.vpad.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    data class Permission(
        val permission: String,
        val permissionName: String,
        val whyWeNeedThis: String,
    )
    private val permissions: Array<Permission> = arrayOf(
        Permission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "写入文件",
            "VPad需要写入文件权限来记录崩溃日志，如果想正常使用VPad，请打开该权限"),
//        Permission(
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            "读取文件",
//            "VPad需要读取文件权限来读取并上报崩溃日志，如果想正常使用VPad，请打开该权限"),
    )

    private val mainViewModel: MainViewModel by viewModels()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        alert("连接异常", "看起来和当前VPad服务器的连接失效或者出现了什么问题！因为：" + exception.message + "\n\n请重新连接", "好的", {d, i ->
            d.dismiss()
            finish()
        }).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mainViewModel.setUpCoroutineExceptionHandler(coroutineExceptionHandler)

        setContentView(R.layout.activity_main)
        // 检测权限，并使用权限列表中的下标作为requestCode
        checkAndRequestPermissions(permissions.map { it.permission }, permissions.mapIndexed {i,p -> i})
        // 检测是否有未捕获异常
        checkIfHasUncaughtException()
        // 设置未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler(this))
    }

    private fun checkIfHasUncaughtException() {
        if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) return
        val crashFile = getCrashLog()
        crashFile?.let {
            if (crashFile.exists()) {
                alert("发现程序异常退出", "发现程序异常退出，是否导出崩溃信息并发送？", "是", {d, i->
                    d.dismiss()
                    share(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider", crashFile))
                }, "不再提醒", {d, i ->
                    d.dismiss()
                    crashFile.delete();
                }, cancelable = false).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode in 0..permissions.size - 1) {
            // 如果无权限
            if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                alert(
                    this.permissions[requestCode].permissionName,
                    this.permissions[requestCode].whyWeNeedThis,
                    "退出程序", {d, i -> finish()},
                    cancelable = false
                ).show()
            } else {
                // 文件权限已经被授予，尝试导出内建preset
                // 如果已经导出过，该方法无任何效果
                mainViewModel.makeSureBuiltinPresetHasExported()
            }
        }
    }

    // Prevent destroy on orientation changed
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

}