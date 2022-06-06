package top.yudoge.vpad.api

import android.util.Log
import top.yudoge.vpadapi.utils.fromByteToInt4
import top.yudoge.vpadapi.utils.fromInt4ToByte
import java.net.Inet4Address
import java.net.NetworkInterface
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * 使用NetworkInterface API的本地IP生成器
 *
 * 获取wlan网卡的子网掩码和设备ip地址，来生成网段内所有的ip
 */
class NetworkInterfacceIPGenerator : LANIPGenerator {

    private fun nthOne(n: Int): Int {
        return (Math.pow(2.0, n.toDouble()) - 1).toInt()
    }

    private fun getMaxAddress(sourceAddress: ByteArray, prefixLength: Short): Int {
        val nthByte = prefixLength / 8
        val nthBit = prefixLength % 8

        // let the bit after nthbyte[nthbit] to 0
        val startIPAddress = sourceAddress.clone()
        startIPAddress[nthByte] = startIPAddress[nthByte].and(
            nthOne(nthBit).shl(8-nthBit).toByte()
        )
        for (i in (nthByte + 1)..3) startIPAddress[i] = 0
        return fromByteToInt4(startIPAddress, 0)
    }
    private fun getMinAddress(sourceAddress: ByteArray, prefixLength: Short): Int {
        val nthByte = prefixLength / 8
        val nthBit = prefixLength % 8
        val endIPAddress = sourceAddress.clone()
        endIPAddress[nthByte] = endIPAddress[nthByte].or(
            nthOne(8-nthBit).shr(nthBit).toByte()
        )
        for (i in (nthByte + 1)..3) endIPAddress[i] = 255.toByte()
        return fromByteToInt4(endIPAddress, 0)
    }

    override fun getIPList(): Iterable<String> {
        val ipList = mutableListOf<String>()
        NetworkInterface.getNetworkInterfaces().asSequence().forEach { iface ->
            // Hardcode
            if (iface.isLoopback || !iface.displayName.startsWith("wlan")) return@forEach
            iface.interfaceAddresses.forEach { ifaceAddr ->
                val addressByteArray = ifaceAddr.address.address
                // Hardcode
                if (addressByteArray.size != 4) return@forEach

                // Iface name starts with 'wlan' and use ipv4
                val prefixLength = ifaceAddr.networkPrefixLength // 24

                val startIpAddress = getMinAddress(addressByteArray, prefixLength)
                val endIpAddress = getMaxAddress(addressByteArray, prefixLength)
                (if (startIpAddress > endIpAddress) (endIpAddress ..startIpAddress - 1) else (startIpAddress ..endIpAddress - 1)).forEach {
                    val ipByteArray = fromInt4ToByte(it)
                    val ipString = Inet4Address.getByAddress(ipByteArray).hostName
                    ipList.add(ipString)
                }
            }
        }
        return ipList
    }
}