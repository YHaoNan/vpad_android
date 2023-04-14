package top.yudoge.vpad.api

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import top.yudoge.vpad.repository.VPadServerRepository
import java.net.NetworkInterface

open class VPadServerScanner(
    private val ips: List<String>,
    private val vPadServerRepository: VPadServerRepository
) {

    suspend fun scanVPadServer(
        lifecycleOwner: LifecycleOwner,
        serverScannedListener: VPadServerScannedListener
    ) {
        var scanJob: Job? = null
        // 生命周期销毁自动停止
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.DESTROYED) {
                    scanJob?.cancel()
                }
            }
        })

        withContext(Dispatchers.IO) {
            scanJob = launch {
                // notify scan has started
                withContext(Dispatchers.Main) { serverScannedListener.onScanStarted() }
                ips.forEach { ipAddress ->
                    if (!isActive) {
                        Log.v("VPadServerScanner", "scan task has cancelled")
                        throw CancellationException("scan task has cancelled")
                    }
                    async {
                        Log.v("VPadServerScanner", "try to send handshake message to ${ipAddress}")
                        try {
                            vPadServerRepository.handshake(ipAddress)?.let {
                                Log.v("VPadServerScanner", "receive hand shake message from ${ipAddress}")
                                withContext(Dispatchers.Main) {
                                    serverScannedListener.onServerScanned(it)
                                }
                            }
                        } catch (e: Throwable) { /*ignore*/ }
                    }
                }
            }
        }
        // notify scan has ended
        serverScannedListener.onScanEnded()


    }
}