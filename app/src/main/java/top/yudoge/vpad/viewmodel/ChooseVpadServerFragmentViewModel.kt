package top.yudoge.vpad.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yudoge.vpad.api.*
import top.yudoge.vpad.repository.VPadServerRepository
import top.yudoge.vpad.toplevel.getIpv4Address
import top.yudoge.vpadapi.VPadServer
import java.net.NetworkInterface
import javax.inject.Inject

@HiltViewModel
class ChooseVpadServerFragmentViewModel @Inject constructor(
    private val vPadServerRepository: VPadServerRepository
) : ViewModel() {

    /**
     * 选择的interface必须有一个ipv4地址，且不能是环回地址
     */
    private val _selectedInterface: MutableLiveData<NetworkInterface> = MutableLiveData()
    val selectedInterface: LiveData<NetworkInterface> = _selectedInterface

    fun selectInterface(iface: NetworkInterface) {
        _selectedInterface.value = iface
    }

    fun setupVPadServer(vPadServer: VPadServer) {
        viewModelScope.launch {
            vPadServerRepository.setupCurrentServer(vPadServer)
        }
    }

    fun autoScanAllServerInNetwork(lifecycleOwner: LifecycleOwner, serverScannedListener: VPadServerScannedListener, iface: NetworkInterface) {
        viewModelScope.launch {
            scanAllServerInIpList(lifecycleOwner, serverScannedListener, NetworkInterfacceIPGenerator().getIPList(iface).toList())
        }
    }

    fun scanAllServerInIpList(lifecycleOwner: LifecycleOwner, serverScannedListener: VPadServerScannedListener, ipList: List<String>) {
        viewModelScope.launch {
            VPadServerScanner(ipList, vPadServerRepository)
                .scanVPadServer(lifecycleOwner, serverScannedListener)
        }
    }

    fun removeCurrentServer() {
        viewModelScope.launch {
            vPadServerRepository.removeCurrentServer()
        }
    }

    fun hasCurrentServer() = vPadServerRepository.currentServer.value != null
}