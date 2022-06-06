package top.yudoge.vpad.api

import top.yudoge.vpadapi.VPadServer


class VirtualVPadServer : VPadServer("no_ip", "VirtualVPadServer", "VPad", 0) {
    override fun label() = "虚拟VPad服务器，仅供测试"
}