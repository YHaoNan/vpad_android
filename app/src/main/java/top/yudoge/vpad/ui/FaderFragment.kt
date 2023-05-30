package top.yudoge.vpad.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import top.yudoge.vpad.R
import top.yudoge.vpad.adapter.FaderGroupAdapter
import top.yudoge.vpad.databinding.FragmentFaderBinding
import top.yudoge.vpad.pojo.Fader
import top.yudoge.vpad.pojo.FaderMode
import top.yudoge.vpad.toplevel.initToFader
import top.yudoge.vpad.viewmodel.FaderViewModel
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpadapi.structure.Message


/**
 * 8个推子，提供Track模式和CC模式
 *
 * Track模式的推子用来控制DAW的实体轨道，具有电平推子、Mute、Solo和Record功能
 * Track模式不能随意分配推子的轨道，八个推子分别对应八个连续的轨道，默认情况下是1~8
 * 可以通过上移和下移操作移动轨道范围
 *
 * CC模式下，Mute、Solo和Record将不可用。
 * CC模式可以随意分配推子的CC控制通道，用户可以通过它们来控制表情、踏板等功能，也可以自行分配，并通过MidiLearn来控制DAW中的特定参数
 * 默认情况下是CC1(ModulationWheel)，CC7(音量控制)，CC10(声相控制)，CC11(表情控制)，CC64(延音踏板)，CC65(滑音开关)，CC3(未定义)，CC9(未定义)
 *
 * FaderFragment需要一个VPadServerCommunicator，同时，这个Communicator必须是已经调用了`connectTo`并连接到VPad服务器的
 *
 * FaderFragment不会去尝试连接这个服务器，它默认已经连接好了，因为它只从PadFragment导航而来，PadFragment持有一个Communicator对象
 */

@AndroidEntryPoint
class FaderFragment : Fragment() {

    private lateinit var binding: FragmentFaderBinding
    private val faderViewModel: FaderViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var faderGroupAdapter: FaderGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // 横屏
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        binding = FragmentFaderBinding.inflate(inflater, container, false)

        return binding.root
    }

    fun sendMessage(message: Message) {
        activityViewModel.sendMessageToServer(message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        faderGroupAdapter = FaderGroupAdapter(requireContext(), ::sendMessage, faderViewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.faderGroup.layoutManager = GridLayoutManager(context, 8)
        binding.faderGroup.adapter = faderGroupAdapter

        binding.masterFader.initToFader(
            Fader(8, 97, FaderMode.Track, 8, "MASTER"),
            ::sendMessage, faderViewModel
        )
        faderViewModel.faders.observe(viewLifecycleOwner) {
            faderGroupAdapter.setFaders(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        faderViewModel.trackMode.observe(viewLifecycleOwner) {
            if (it) {
                menu.removeGroup(GROUP_CC_MODE_MENU)
                menu.add(GROUP_TRACK_MODE_MENU, MENU_TRACK_BAND_LEFT, 0, "<").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.add(GROUP_TRACK_MODE_MENU, MENU_TRACK_BAND_RIGHT, 1, ">").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.add(GROUP_TRACK_MODE_MENU, MENU_MASTER_TRACK, 2, R.string.master_track).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.add(GROUP_TRACK_MODE_MENU, MENU_TURN_TO_CC, 3, R.string.cc_mode).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            } else {
                menu.removeGroup(GROUP_TRACK_MODE_MENU)
                menu.add(GROUP_CC_MODE_MENU, MENU_TURN_TO_TRACK, 0, R.string.track_mode).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            MENU_TURN_TO_CC -> faderViewModel.turnToCCMode()
            MENU_TURN_TO_TRACK -> faderViewModel.turnToTrackMode()
            MENU_TRACK_BAND_LEFT -> sendMessage(faderViewModel.trackBankLeftMessage())
            MENU_TRACK_BAND_RIGHT -> sendMessage(faderViewModel.trackBankRightMessage())
            MENU_MASTER_TRACK -> binding.drawerLayout.openDrawer(binding.rightDrawer)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "FaderFragment"
        const val GROUP_TRACK_MODE_MENU = 0
        const val GROUP_CC_MODE_MENU = 1
        const val MENU_TURN_TO_CC = 0
        const val MENU_TRACK_BAND_LEFT = 1
        const val MENU_TRACK_BAND_RIGHT = 2
        const val MENU_MASTER_TRACK = 3
        const val MENU_TURN_TO_TRACK = 4
    }
}
