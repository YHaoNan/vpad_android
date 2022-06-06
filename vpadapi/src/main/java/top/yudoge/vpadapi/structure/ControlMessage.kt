package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations

class ControlMessage(
    var operation: Int,
    var state: Int,
    var autoClose: Int
) : Message() {

    init {
        this.op = Operations.OP_CONTROLMSG
    }
    companion object {
        const val STATE_ON = 1
        const val STATE_OFF = 0
        const val AUTO_CLOSE_ON = 1
        const val AUTO_CLOSE_OFF = 0

    }


    override fun bodyToBytes(): ByteArray {
        return byteArrayOf(
            operation.toByte(), state.toByte(), autoClose.toByte()
        )
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.operation = bytes[offset].toInt()
        this.state = bytes[offset + 1].toInt()
        this.autoClose = bytes[offset + 2].toInt()
        return 3
    }

    override fun toString(): String {
        return "ControlMessage(operation=$operation, state=$state, autoClose=$autoClose)"
    }

}