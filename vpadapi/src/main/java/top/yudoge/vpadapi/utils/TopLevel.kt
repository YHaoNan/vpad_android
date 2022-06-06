package top.yudoge.vpadapi.utils

import top.yudoge.vpadapi.structure.Int2
import top.yudoge.vpadapi.structure.Int4

fun fromByteToInt4(byte: ByteArray, offset: Int): Int {
    val int4 = Int4()
    int4.fromBytes(byte, offset)
    return int4.value()
}

fun fromInt4ToByte(int: Int): ByteArray {
    val int4 = Int4()
    int4.num = int
    return int4.toBytes()
}

fun fromInt2ToByte(int: Short): ByteArray {
    val int2 = Int2()
    int2.num = int
    return int2.toBytes()
}

fun fromByteToInt2(bytes: ByteArray, offset: Int): Short {
    val int2 = Int2()
    int2.fromBytes(bytes, offset)
    return int2.value()
}