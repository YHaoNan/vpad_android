package top.yudoge.vpad.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import top.yudoge.vpad.adapter.ServerListAdapter
import top.yudoge.vpad.adapter.ServerMode
import top.yudoge.vpad.api.VPadServerCommunicator
import top.yudoge.vpad.api.VPadServerScannedListener
import top.yudoge.vpad.api.VirtualVPadServer
import top.yudoge.vpad.databinding.FragmentChooseVpadServerBinding
import top.yudoge.vpad.toplevel.showListDialog
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
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ServerListAdapter
    private lateinit var swipeRefresher: SwipeRefreshLayout
    @Inject lateinit var vpadClient: VPadClient

    private val onServerScannedListener = object : VPadServerScannedListener {
        override fun onScanStarted() {
            swipeRefresher.isRefreshing = true
            // 清空之前的列表
            adapter.clearVpadServer()
            // 添加虚拟Server
            onServerScanned(VirtualVPadServer())
            Log.i(TAG, "ONSCANSTARTED")
        }

        override fun onServerScanned(server: VPadServer) {
            adapter.addVpadServer(server)
            Log.i(TAG, "ONSERVERSCANNED ${server.toString()}")
        }

        override fun onScanEnded() {
            swipeRefresher.isRefreshing = false
            Log.i(TAG, "ONSCANENDED")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 竖屏
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        val binding = FragmentChooseVpadServerBinding.inflate(inflater, container, false)

        adapter = ServerListAdapter { serverMode, vPadServer ->
            val oldCommunicator = activityViewModel.getCommunicator()
            if (oldCommunicator != null) {
                lifecycleScope.launch {
                    oldCommunicator.close();
                    activity!!.lifecycle.removeObserver(oldCommunicator)
                    initCommunicator(vPadServer)
                }
            } else {
                initCommunicator(vPadServer)
            }
            navigateByServerMode(serverMode, vPadServer)
        }

        binding.serverList.adapter = adapter

        swipeRefresher = binding.swipeRefresher
        context?.showListDialog("选择网络接口", NetworkInterface.getNetworkInterfaces().toList(), NetworkInterface::getDisplayName) { iface ->
            swipeRefresher.setOnRefreshListener {
                viewModel.scanVPadServer(viewLifecycleOwner, onServerScannedListener, iface)
            }
            viewModel.scanVPadServer(viewLifecycleOwner, onServerScannedListener, iface)
        }
        return binding.root
    }

    private fun initCommunicator(vPadServer: VPadServer) {
        // 装配VPadServer实例
        activityViewModel.setUpVPadServer(vPadServer)
        // 创建ServerCommunicator
        val serverCommunicatior = VPadServerCommunicator(vpadClient, activityViewModel)
        // 向activity的生命周期中添加观察者
        activity!!.lifecycle.addObserver(serverCommunicatior)
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
    companion object {
        const val TAG = "ChooseVpadServerFragment"
    }
}