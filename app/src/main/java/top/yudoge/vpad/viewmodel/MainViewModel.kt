package top.yudoge.vpad.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import top.yudoge.vpad.domain.PresetDomain
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.repository.PresetFileRepository
import top.yudoge.vpad.repository.VPadServerRepository
import top.yudoge.vpad.toplevel.gson
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.Message
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * MainViewModel是MainActivity绑定的ViewModel，程序中所有界面都是在MainActivity中的Fragment，所以，它贯穿整个应用程序
 *
 * 为了避免每一个子ViewModel重复连接服务器造成不必要的损失，连接服务器、发送信息，统一由MainViewModel负责
 *
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val vPadServerRepository: VPadServerRepository,
    private val presetDomain: PresetDomain,
    private @ApplicationContext val context: Context
): ViewModel() {
    private lateinit var globalCoroutineExceptionHandler: CoroutineExceptionHandler;
    val currentServer: LiveData<VPadServer?> = vPadServerRepository.currentServer

    fun sendMessageToServer(message: Message) = viewModelScope.launch(globalCoroutineExceptionHandler) {
        vPadServerRepository.sendMessage(message)
    }


    fun makeSureBuiltinPresetHasExported() {
        viewModelScope.launch {
            // 如果文件夹已经存在，代表已经建立
            if (File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), top.yudoge.vpad.toplevel.Constants.PRESET_DIR).exists()) return@launch

            top.yudoge.vpad.toplevel.Constants.BUILTIN_PRESET_FILE_NAMES.forEach {
                val fileContent = InputStreamReader(context.assets.open(it)).readText()
                val preset = gson.fromJson(fileContent, Preset::class.java)
                presetDomain.addPreset(preset)
            }
        }
    }

    fun setUpCoroutineExceptionHandler(coroutineExceptionHandler: CoroutineExceptionHandler) {
        globalCoroutineExceptionHandler = coroutineExceptionHandler
    }

    fun cleanUpConnectedServer() = viewModelScope.launch {
        if (vPadServerRepository.currentServer.value!=null) {
            vPadServerRepository.removeCurrentServer()
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }

}