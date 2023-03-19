package top.yudoge.vpad.api

import top.yudoge.vpadapi.utils.fromByteToInt4
import java.net.NetworkInterface
import kotlin.experimental.and
import kotlin.experimental.or

interface LANIPGenerator {
    fun getIPList(iface: NetworkInterface): Iterable<String>
}