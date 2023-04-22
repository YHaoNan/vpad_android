package top.yudoge.vpad.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.king.zxing.CaptureFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import top.yudoge.vpad.R
import top.yudoge.vpad.adapter.ServerListAdapter
import top.yudoge.vpad.adapter.ServerMode
import top.yudoge.vpad.api.VPadServerCommunicator
import top.yudoge.vpad.api.VPadServerScannedListener
import top.yudoge.vpad.api.VirtualVPadServer
import top.yudoge.vpad.databinding.FragmentChooseVpadServerBinding
import top.yudoge.vpad.toplevel.allIpv4AndNotLoopbackIfaces
import top.yudoge.vpad.toplevel.getLabel
import top.yudoge.vpad.toplevel.showListDialog
import top.yudoge.vpad.ui.CustomCaptureFragment.Companion.QRSCAN_BUNDLE_KEY
import top.yudoge.vpad.ui.CustomCaptureFragment.Companion.QRSCAN_REQUEST_KEY
import top.yudoge.vpad.viewmodel.ChooseVpadServerFragmentViewModel
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpadapi.VPadClient
import top.yudoge.vpadapi.VPadServer
import java.net.NetworkInterface
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("LongLogTag")
class ChooseVpadServerFragment : Fragment() {

    private val viewModel: ChooseVpadServerFragmentViewModel by viewModels()
    @Inject lateinit var vpadClient: VPadClient

    override fun onResume() {
        super.onResume()
        if (viewModel.hasCurrentServer())
            viewModel.removeCurrentServer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        // 竖屏
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContent {
            chooseInitialIface()
            Page()
        }
    }

    @Composable
    fun Page() {
        val serverList = remember { mutableStateListOf<VPadServer>(VirtualVPadServer()) }
        val selecteIface by viewModel.selectedInterface.observeAsState()
        var isScanButtonLoading by remember { mutableStateOf(false) }

        // 扫描回调
        val serverScannedListener =  object : VPadServerScannedListener {
            override fun onScanStarted() {
                isScanButtonLoading = true
                serverList.clear()
                serverList.add(VirtualVPadServer())
            }

            override fun onServerScanned(server: VPadServer) {
                serverList.add(server)
            }

            override fun onScanEnded() {
                isScanButtonLoading = false
            }
        }

        // 处理扫码结果
        setFragmentResultListener(QRSCAN_REQUEST_KEY) { key, bundle ->
            val result = bundle.getString(QRSCAN_BUNDLE_KEY)
            if (result == null) {
                Toast.makeText(requireContext(), "二维码格式不正确", Toast.LENGTH_SHORT).show()
                return@setFragmentResultListener
            }

            val ips = result.split(";")
            if (ips.size == 0) {
                Toast.makeText(requireContext(), "二维码格式不正确", Toast.LENGTH_SHORT).show()
                return@setFragmentResultListener
            }

            Toast.makeText(requireContext(), "正在扫描二维码中的所有VPadServer...", Toast.LENGTH_SHORT).show()

            viewModel.scanAllServerInIpList(viewLifecycleOwner, serverScannedListener, ips)
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { scanQRCode() }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24), contentDescription = "")
                }
            },
            isFloatingActionButtonDocked = true,
            bottomBar = {
                BottomBar(selecteIface, isScanButtonLoading, serverScannedListener)
            }
        ) {
            it.calculateBottomPadding()
            LazyColumn {
                items(serverList.size) {index ->
                    ServerListItem(vPadServer = serverList[index])
                }
            }
        }
    }
    @Composable
    fun BottomBar(selectedIface: NetworkInterface?, isScanButtonLoading: Boolean, serverScannedListener: VPadServerScannedListener) {

        BottomAppBar(cutoutShape = MaterialTheme.shapes.small.copy(
            CornerSize(percent = 50)
        )) {
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "iface: ${selectedIface?.getLabel() ?: "Not Selected"}", modifier = Modifier
                    .weight(1f)
                    .clickable {
                        chooseNetworkInterface()
                    })
                LoadingTextButton(text = "扫描网络", isLoading = isScanButtonLoading) {
                    if (selectedIface == null)
                        Toast.makeText(requireContext(), "请先选择网络接口", Toast.LENGTH_SHORT).show()
                    else
                        scanServerByIface(selectedIface, serverScannedListener)
                }
            }
        }
    }

    private fun chooseNetworkInterface() {
        requireActivity().showListDialog("选择网络接口",
            NetworkInterface.getNetworkInterfaces().toList().allIpv4AndNotLoopbackIfaces(),
            NetworkInterface::getLabel
        ) {
            viewModel.selectInterface(it)
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ServerListItem(vPadServer: VPadServer) {
        Column(
            Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        viewModel.setupVPadServer(vPadServer)
                        navigateByServerMode(ServerMode.Pad, vPadServer)
                    },
                    onLongClick = {
                        viewModel.setupVPadServer(vPadServer)
                        navigateByServerMode(ServerMode.Fader, vPadServer)
                    }
                )
                .padding(10.dp)
        ) {
            Text(text = vPadServer.name, fontSize = 15.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = vPadServer.label(), color = MaterialTheme.colors.onSurface, fontSize = 12.sp)
        }
    }

    @Composable
    fun LoadingTextButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
        TextButton(
            onClick = {
                onClick()
            },
            colors = ButtonDefaults.buttonColors(disabledBackgroundColor = Color.Transparent),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp),
                    color = Color.White,
                )
            } else {
                Text(text)
            }
        }
    }

    private fun scanQRCode() {
        findNavController().navigate(
            ChooseVpadServerFragmentDirections.actionChooseVpadServerFragmentToCustomCaptureFragment()
        )
    }

    private fun scanServerByIface(iface: NetworkInterface, serverScannedListener: VPadServerScannedListener) {
        viewModel.autoScanAllServerInNetwork(viewLifecycleOwner, serverScannedListener, iface)
    }

    private fun navigateByServerMode(serverMode: ServerMode, vPadServer: VPadServer) {
        when(serverMode) {
            ServerMode.Pad -> {
                findNavController().navigate(
                    ChooseVpadServerFragmentDirections.actionChooseVpadServerFragmentToPadFragment(
                        vPadServer
                    )
                )
            }
            ServerMode.Fader -> {
                findNavController().navigate(
                    ChooseVpadServerFragmentDirections.actionChooseVpadServerFragmentToFaderFragment(
                        vPadServer
                    )
                )
            }
        }

    }

    private fun chooseInitialIface() {
        NetworkInterface.getNetworkInterfaces()
            .toList()
            .allIpv4AndNotLoopbackIfaces()
            .firstOrNull { it.displayName.indexOf("wlan") != -1 || it.displayName.indexOf("ap") != -1 }
            ?.let {
                viewModel.selectInterface(it)
            }
    }

    companion object {
        const val TAG = "ChooseVpadServerFragment"
        const val QRSCAN_REQUEST_KEY = "QRSCAN_RK"
        const val QRSCAN_BUNDLE_KEY = "QRSCAN_BK"
    }
}