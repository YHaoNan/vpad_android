package top.yudoge.vpadapi.structure

import top.yudoge.vpadapi.Operations
import top.yudoge.vpadapi.utils.Utils
import top.yudoge.vpadapi.utils.fromByteToInt2
import top.yudoge.vpadapi.utils.fromInt2ToByte

class ChordMessage(
    var note: Int,
    var velocity: Int,
    var state: Int,
    var bpm: Int,
    var chordType: Int,
    var chordLevel: Int,
    var transpose: Int,
    var arpDelay: Int
) : Message() {
    init {
        this.op = Operations.OP_CHORDMESSAGE
    }
    override fun bodyToBytes(): ByteArray {
        return Utils.concatBytes(
            byteArrayOf(note.toByte(), velocity.toByte(), state.toByte(), chordType.toByte(), chordLevel.toByte(), transpose.toByte(), arpDelay.toByte()),
            fromInt2ToByte(bpm.toShort()))
    }

    override fun bodyFromBytes(bytes: ByteArray, offset: Int): Int {
        this.note = bytes[offset].toInt()
        this.velocity = bytes[offset + 1].toInt()
        this.state = bytes[offset + 2].toInt()
        this.chordType = bytes[offset + 3].toInt()
        this.chordLevel = bytes[offset + 4].toInt()
        this.transpose = bytes[offset + 5].toInt()
        this.arpDelay = bytes[offset + 6].toInt()
        this.bpm = fromByteToInt2(bytes, 7).toInt()
        return 9
    }

    override fun toString(): String {
        return "ChordMessage(note=$note, velocity=$velocity, state=$state, chordType=$chordType, transpose=$transpose, arpDelay=$arpDelay)"
    }

}