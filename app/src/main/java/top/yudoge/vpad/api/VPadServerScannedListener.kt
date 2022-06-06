package top.yudoge.vpad.api

import top.yudoge.vpadapi.VPadServer

interface VPadServerScannedListener {
    fun onScanStarted()
    fun onServerScanned(server: VPadServer)
    fun onScanEnded()
}