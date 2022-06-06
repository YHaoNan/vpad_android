package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations

class PitchWheelMessage(
    var pos: Int,
    var prevPos: Int
) : Message() {

    init {
        this.op = Operations.OP_PITCHWHEELMESSAGE
    }

    override fun bodyToBytes(): ByteArray {
        return byteArrayOf(
            pos.toByte(), prevPos.toByte()
        )
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.pos = bytes[offset].toInt()
        this.prevPos = bytes[offset + 1].toInt()
        return 2
    }
}