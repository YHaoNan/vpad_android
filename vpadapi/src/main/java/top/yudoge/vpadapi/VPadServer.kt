package top.yudoge.vpadapi

import java.io.Serializable

/**
 * @param ip 服务器ip地址
 * @param name 服务器名字
 * @param platform 服务器平台
 * @param latency 服务器延迟 必须说明的是，这个延迟仅是扫描时两端通信的延时，仅可以作为参考
 */
open class VPadServer(val ip: String, val name: String, val platform: String, val latency: Number) : Serializable {
    open fun label() = "IP: ${ip}, Platform: ${platform}, Latency: ${latency}ms"
    override fun toString(): String {
        return label()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VPadServer

        if (ip != other.ip) return false
        if (name != other.name) return false
        if (platform != other.platform) return false
//        if (latency != other.latency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + platform.hashCode()
        result = 31 * result + latency.hashCode()
        return result
    }

}