package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations
import top.yudoge.vpadapi.utils.Utils
import top.yudoge.vpadapi.utils.fromByteToInt2
import top.yudoge.vpadapi.utils.fromInt2ToByte

class ArpMessage(
    var note: Int,
    var velocity: Int,
    var state: Int,
    var method: Int,
    var rate: Int,
    var swingPct: Int,
    var upNoteCnt: Int,
    var velocityAutomation: Int,
    var dynamicPct: Int,
    var bpm:Int,
    var channel: Int
) : Message() {

    init {
        this.op = Operations.OP_ARPMESSAGE
    }

    override fun bodyToBytes(): ByteArray {
        return Utils.concatBytes(Utils.concatBytes(
            byteArrayOf(
                note.toByte(), velocity.toByte(), state.toByte(),
                method.toByte(), rate.toByte(), swingPct.toByte(),
                upNoteCnt.toByte(), velocityAutomation.toByte(),
            ), Utils.concatBytes(
                fromInt2ToByte(dynamicPct.toShort()), fromInt2ToByte(bpm.toShort())
            )
        ), byteArrayOf(channel.toByte()))
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.note = bytes[offset].toInt()
        this.velocity = bytes[offset + 1].toInt()
        this.state = bytes[offset + 2].toInt()
        this.method = bytes[offset + 3].toInt()
        this.rate = bytes[offset + 4].toInt()
        this.swingPct = bytes[offset + 5].toInt()
        this.upNoteCnt = bytes[offset + 6].toInt()
        this.velocityAutomation = bytes[offset + 7].toInt()
        this.dynamicPct = fromByteToInt2(bytes, 8).toInt()
        this.bpm = fromByteToInt2(bytes, 10).toInt()
        this.channel = bytes[offset + 12].toInt()
        return 13
    }

    override fun toString(): String {
        return "ArpMessage(note=$note, velocity=$velocity, state=$state, method=$method, rate=$rate, swingPct=$swingPct, upNoteCnt=$upNoteCnt, velocityAutomation=$velocityAutomation, dynamicPct=$dynamicPct, bpm=$bpm, channel=$channel)"
    }

}