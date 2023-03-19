package top.yudoge.vpad.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import top.yudoge.vpadapi.*
import top.yudoge.vpadapi.structure.HandShakeMessage
import top.yudoge.vpadapi.structure.Int4
import top.yudoge.vpadapi.utils.fromByteToInt4
import top.yudoge.vpadapi.utils.fromInt4ToByte
import java.lang.Exception
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

class VPadServerScanner(
    private val lanipGenerator: LANIPGenerator
) {

    // Not correct...
    suspend fun scanVPadServer(
        lifecycleOwner: LifecycleOwner,
        serverScannedListener: VPadServerScannedListener,
        iface: NetworkInterface
    ) {
        var scanJob: Job? = null
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
                lanipGenerator.getIPList(iface).forEach { ipAddress ->
                    if (!isActive) {
                        Log.v(TAG, "scan task has cancelled")
                        throw CancellationException("scan task has cancelled")
                    }
                    async {
                        Log.v(TAG, "try to send handshake message to ${ipAddress}")
                        val handshakeMessage = HandShakeMessage("Android", "Android")
                        Log.v(TAG, Arrays.toString(handshakeMessage.toBytes()))
                        var connection: VPadConnection? = null
                        try {
                            connection = DefaultVPadClient().connect(
                                VPadServer(ipAddress, "", "", 0)
                            )
                            val startTime = System.currentTimeMillis()
                            connection.sendMessage(handshakeMessage)
                            val latency = System.currentTimeMillis() - startTime
                            val result = HandShakeMessage()
                            connection.readMessage(result)
                            // notify server scanned
                            withContext(Dispatchers.Main) {
                                serverScannedListener.onServerScanned(
                                    VPadServer(ipAddress, result.name, result.platform, latency)
                                )
                            }
                        } catch (e: Exception) {
//                                    Log.v(TAG, "Got an exception when scan ${ipString}" + e.message)
                        } finally {
                            connection?.disconnect()
                        }
                    }
                }
            }
        }
        // notify scan has ended
        serverScannedListener.onScanEnded()
    }

    companion object {
        const val TAG = "VPadServerScanner"
    }
}