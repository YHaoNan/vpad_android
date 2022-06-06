package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations


class TrackMessage(
    var nth: Int,
    var state: Int,
    var value: Int
) : Message() {
    init {
        this.op = Operations.OP_TRACKMESSAGE
    }

    override fun bodyToBytes(): ByteArray {
        return byteArrayOf(
            nth.toByte(), state.toByte(), value.toByte()
        )
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.nth = bytes[offset].toInt()
        this.state = bytes[offset + 1].toInt()
        this.value = bytes[offset + 2].toInt()
        return 3
    }
}