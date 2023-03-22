package top.yudoge.vpad.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.CommunicatorHolder
import top.yudoge.vpad.api.VPadServerCommunicator
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.Message
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 * MainViewModel是MainActivity绑定的ViewModel，程序中所有界面都是在MainActivity中的Fragment，所以，它贯穿整个应用程序
 *
 * 为了避免每一个子ViewModel重复连接服务器造成不必要的损失，连接服务器、发送信息，统一由MainViewModel负责
 *
 */
@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel(), CommunicatorHolder {

    private var vpadServer: VPadServer? = null
    private var communicator: VPadServerCommunicator? = null
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler

    fun setUpVPadServer(vpadServer: VPadServer) {
        this.vpadServer = vpadServer
    }

    fun setUpCoroutineExceptionHandler(handler: CoroutineExceptionHandler) {
        this.coroutineExceptionHandler = handler
    }
    fun getCommunicator(): VPadServerCommunicator? {
        return communicator
    }

    override fun setUpCommunicator(communicator: VPadServerCommunicator) {
        this.communicator = communicator
    }

    override fun canConnectNow() {
        if (vpadServer == null) throw IllegalStateException("When communicator can connect, a vpadServer instance must passed via `setUpVPadServer`")
        if (communicator == null) throw IllegalStateException("canConnectNow must call via `VPadCommunicator`")
        viewModelScope.launch(coroutineExceptionHandler) {
            communicator!!.connectTo(vpadServer!!)
        }
    }

    override fun mustDisconnect() {
        if (communicator == null) throw IllegalStateException("mustDisconnect must call via `VPadCommunicator`")
        viewModelScope.launch(coroutineExceptionHandler) {
            communicator!!.close()
        }
    }


    fun sendMessageToServer(message: Message) = viewModelScope.launch(coroutineExceptionHandler) {
        if (communicator == null) throw IllegalStateException("No communicator instance")
        Log.d(TAG, "send message to server" + message.toString())
        communicator!!.sendMessageToServer(message)
    }

    companion object {
        const val TAG = "MainViewModel"
    }

}