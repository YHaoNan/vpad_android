package top.yudoge.vpad.api

import android.util.Log
import top.yudoge.vpadapi.VPadConnection
import top.yudoge.vpadapi.structure.Message


class VirtualVPadConnection : VPadConnection {
    private var isClosed = false
    override fun disconnect() {
        isClosed = true
    }

    override fun sendMessage(msg: Message?) {
        Log.v(TAG, "Send an message to server: ${msg.toString()}")
    }

    override fun readMessage(msg: Message?) {}

    override fun isClosed() = isClosed

    companion object {
        const val TAG = "VirtualVPadConnection"
    }
}