package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations

class CCMessage(
    var channel: Int,
    var value: Int
) : Message() {
    init {
        this.op = Operations.OP_CCEVENT
    }

    override fun bodyToBytes(): ByteArray {
        return byteArrayOf(
            channel.toByte(), value.toByte()
        )
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.channel = bytes[offset].toInt()
        this.value = bytes[offset + 1].toInt()
        return 2
    }
}