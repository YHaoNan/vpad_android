package top.yudoge.vpad.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.CommunicatorHolder
import top.yudoge.vpad.api.VPadServerCommunicator
import top.yudoge.vpad.repository.PresetRepository
import top.yudoge.vpad.repository.VPadServerRepository
import top.yudoge.vpad.toplevel.safeGetPresetDir
import top.yudoge.vpadapi.Constants
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.Message
import java.io.File
import java.io.InputStreamReader
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 * MainViewModel是MainActivity绑定的ViewModel，程序中所有界面都是在MainActivity中的Fragment，所以，它贯穿整个应用程序
 *
 * 为了避免每一个子ViewModel重复连接服务器造成不必要的损失，连接服务器、发送信息，统一由MainViewModel负责
 *
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    val presetRepository: PresetRepository,
    val vPadServerRepository: VPadServerRepository,
    @ApplicationContext val context: Context
): ViewModel() {
    private lateinit var globalCoroutineExceptionHandler: CoroutineExceptionHandler;

    fun sendMessageToServer(message: Message) = viewModelScope.launch(globalCoroutineExceptionHandler) {
        vPadServerRepository.sendMessage(message)
    }


    fun makeSureBuiltinPresetHasExported() {
        // 如果文件夹已经存在，代表已经建立
        if (File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), top.yudoge.vpad.toplevel.Constants.PRESET_DIR).exists()) return

        top.yudoge.vpad.toplevel.Constants.BUILTIN_PRESET_FILE_NAMES.forEach {
            val presetName = it.substring(it.lastIndexOf("/") + 1, it.indexOf("."))
            presetRepository.addPresetByJson(
                InputStreamReader(context.assets.open(it)).readText(),
                presetName, false
            )
        }
    }

    fun setUpCoroutineExceptionHandler(coroutineExceptionHandler: CoroutineExceptionHandler) {
        globalCoroutineExceptionHandler = coroutineExceptionHandler
    }

    companion object {
        const val TAG = "MainViewModel"
    }

}