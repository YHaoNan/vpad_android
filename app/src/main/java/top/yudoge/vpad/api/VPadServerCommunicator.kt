package top.yudoge.vpad.api

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yudoge.vpad.viewmodel.PadViewModel
import top.yudoge.vpadapi.VPadClient
import top.yudoge.vpadapi.VPadConnection
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.Message

interface CommunicatorHolder {
    fun setUpCommunicator(communicatior: VPadServerCommunicator)
    fun canConnectNow()
    fun mustDisconnect()
}

class VPadServerCommunicator constructor(
    private val vPadClient: VPadClient,
    private val communicatorHolder: CommunicatorHolder,
) : DefaultLifecycleObserver {
    private var vpadConnection: VPadConnection? = null
    private var vpadServer: VPadServer? = null

    fun targetIs(vpadServer: VPadServer) = this.vpadServer == vpadServer

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        communicatorHolder.setUpCommunicator(this)
        communicatorHolder.canConnectNow()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        communicatorHolder.mustDisconnect()
    }

    suspend fun connectTo(vpadServer: VPadServer) {
        this.vpadServer = vpadServer
        withContext(Dispatchers.IO) {
            if (vpadServer is VirtualVPadServer) vpadConnection = VirtualVPadConnection()
            else vpadConnection = vPadClient.connect(vpadServer)
        }
    }

    suspend fun sendMessageToServer(message: Message) {
        Log.i("VSComm", this.hashCode().toString() + ", " + vpadConnection)
        withContext(Dispatchers.IO) {
            vpadConnection?.sendMessage(message)
        }
    }

    suspend fun close() {
        withContext(Dispatchers.IO) {
            vpadConnection?.disconnect()
            vpadConnection = null
        }
    }
}