package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations

class PitchWheelMessage(
    var pos: Int,
    var prevPos: Int,
    var channel: Int
) : Message() {

    init {
        this.op = Operations.OP_PITCHWHEELMESSAGE
    }

    override fun bodyToBytes(): ByteArray {
        return byteArrayOf(
            pos.toByte(), prevPos.toByte(), channel.toByte()
        )
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.pos = bytes[offset].toInt()
        this.prevPos = bytes[offset + 1].toInt()
        this.channel = bytes[offset + 2].toInt()
        return 3
    }
}