package top.yudoge.vpad.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yudoge.vpadapi.ClientException
import top.yudoge.vpadapi.DefaultVPadClient
import top.yudoge.vpadapi.VPadClient
import top.yudoge.vpadapi.VPadConnection
import top.yudoge.vpadapi.VPadServer
import top.yudoge.vpadapi.structure.HandShakeMessage
import top.yudoge.vpadapi.structure.Message
import java.net.InetAddress
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton


/**
 * 负责持有到VPadServer的连接
 * 它和Application的生命周期绑定，当Application销毁时，若有一个到某一server的连接，断开连接
 */
@Singleton
class VPadServerRepository @Inject constructor(
    private val vPadClient: VPadClient
) {


    // 当前服务器，当前有可能没连接任何服务器，很多操作在没连接服务器时不能进行
    private val _currentServer: MutableLiveData<VPadServer?> = MutableLiveData()
    val currentServer: LiveData<VPadServer?> = _currentServer
    @Volatile
    private var currentConn: VPadConnection? = null

    /**
     * 向一个ip地址以及端口发送handshake消息，若对方返回了一个handshake消息，将它解析成一个VPadServer实例
     * 否则返回null，注意，该方法不会保存握手的VPadServer的连接
     * @throws ClientException 若握手的连接过程中发生任何异常导致无法连接
     *                         若发信的过程中有任何异常
     * @return 若对方返回了一个HandShakeMessage，则构造对应的VPadServer，否则返回null
     */
    suspend fun handshake(ipAddress: String): VPadServer? {
        // 连接
        val conn = connectTo(ipAddress)

        // 暂时忽略解析消息失败的问题，假设总会成功
        val startTime = System.currentTimeMillis();
        conn.sendMessage(HandShakeMessage("VPadClient", "Android"))
        HandShakeMessage().apply {
            conn.readMessage(this)
            val elps = System.currentTimeMillis() - startTime;
            conn.disconnect()
            return VPadServer(ipAddress, name, platform, elps)
        }

    }

    /**
     * 连接到某一个VPadServer上，并设置为当前VPadServer
     * @throws ClientException 若连接过程中发生任何异常导致无法连接
     */
    suspend fun setupCurrentServer(vPadServer: VPadServer) {
        if (_currentServer.value!=null) throw ClientException("Holding a server now, please remove it first")
        withContext(Dispatchers.IO) {
            currentConn = connectTo(vPadServer.ip)
            _currentServer.postValue(vPadServer)
        }
    }

    /**
     * 给当前vpadServer发送一条消息
     * @throws ClientException 若发信过程中发生任何异常导致无法发送
     */
    suspend fun sendMessage(message: Message) {
        if (currentConn == null) throw ClientException("Not connect to any server yet")
        withContext(Dispatchers.IO) {
            currentConn!!.sendMessage(message)
        }
    }

    /**
     * 清除当前server
     * @throws ClientException 若发信过程中发生任何异常导致无法发送
     */
    suspend fun removeCurrentServer() {
        if (_currentServer.value == null) throw ClientException("Not connect to any server yet")
        withContext(Dispatchers.IO) {
            currentConn!!.disconnect()
            currentConn = null
            _currentServer.postValue(null)
        }
    }

    /**
     * 连接到inetAddress
     * @throws ClientException 若连接过程中发生任何异常导致无法连接
     * @return 代表此次连接的VPadConnection对象
     */
    private suspend fun connectTo(ipAddress: String) = vPadClient.connect(VPadServer(ipAddress, "", "", 0))

}