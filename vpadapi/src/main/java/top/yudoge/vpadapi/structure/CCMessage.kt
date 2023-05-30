package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations

class CCMessage(
    var channel: Int,
    var value: Int,
    var channel2: Int
) : Message() {
    init {
        this.op = Operations.OP_CCEVENT
    }

    override fun bodyToBytes(): ByteArray {
        return byteArrayOf(
            channel.toByte(), value.toByte(), channel2.toByte()
        )
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.channel = bytes[offset].toInt()
        this.value = bytes[offset + 1].toInt()
        this.channel2 = bytes[offset + 2].toInt()
        return 3
    }
}