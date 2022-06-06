package top.yudoge.vpad.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import top.yudoge.vpad.databinding.FragmentPadSettingBinding
import top.yudoge.vpad.view.setting_view.SettingView
import top.yudoge.vpad.viewmodel.PadSettingViewModel

@AndroidEntryPoint
class PadSettingFragment : Fragment() {

    private val padSettingViewModel: PadSettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 竖屏
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val binding = FragmentPadSettingBinding.inflate(inflater, container, false).apply {
            settingView.settingItemChangedListener = SettingView.OnSettingItemChangedListener { settingItem ->
                padSettingViewModel.updateSettingItem(settingItem)
            }
            padSettingViewModel.settingItems.observe(viewLifecycleOwner) {
                settingView.setSettingItem(it)
            }
        }
        return binding.root
    }

    companion object {
        const val TAG = "PadSettingFragment"
    }
}