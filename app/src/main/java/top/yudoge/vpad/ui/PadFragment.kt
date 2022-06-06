package top.yudoge.vpad.ui

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import top.yudoge.vpad.R
import top.yudoge.vpad.adapter.ButtonGroupAdapter
import top.yudoge.vpad.adapter.PadContainerAdapter
import top.yudoge.vpad.databinding.FragmentPadBinding
import top.yudoge.vpad.di.Pixel
import top.yudoge.vpad.padtheme.AKaiMPD218PadThemeInitializer
import top.yudoge.vpad.pojo.ButtonLabel
import top.yudoge.vpad.toplevel.alert
import top.yudoge.vpad.toplevel.setOnWheelStateChangeListener
import top.yudoge.vpad.toplevel.showInputDialog
import top.yudoge.vpad.view.PitchWheelWheelStateChangeListener
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpad.viewmodel.PadViewModel
import top.yudoge.vpadapi.structure.MidiMessage
import java.io.IOError
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
class PadFragment : Fragment() {

    private val padViewModel: PadViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentPadBinding
    lateinit var buttonGroupAdapter: ButtonGroupAdapter

    @Inject @Pixel lateinit var pixelTypeface: Typeface

    private val buttonGroupClickListener: (ButtonLabel) -> Unit = {
        when(it.label) {
            "Rgn+" -> padViewModel.increaseNoteRegion()
            "Rgn-" -> padViewModel.decreaseNoteRegion()
            "PDST" -> padViewModel.openSettingMode()
            "BPM" -> context?.showInputDialog("设置BPM", value = buttonGroupAdapter.getBpm().toString(), null) {
                padViewModel.setBpm(it.toInt())
            }
            "PLAY", "STOP", "REC", "LOOP", "UNDO", "REDO", "CLK", "SAVE" -> {
                padViewModel.getControlMessageByLabel(it.label)?.let {
                    activityViewModel.sendMessageToServer(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 竖屏
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = FragmentPadBinding.inflate(inflater, container, false)

        bindView()

        return binding.root
    }

    private fun bindView() {
        binding.lifecycleOwner = viewLifecycleOwner

        binding.serverLabel.setTypeface(pixelTypeface)
        binding.message.setTypeface(pixelTypeface)

        binding.vm = padViewModel

        buttonGroupAdapter = ButtonGroupAdapter(padViewModel, viewLifecycleOwner, buttonGroupClickListener)
        binding.buttonGroup.adapter = buttonGroupAdapter

        // 因为之前有考虑过添加调制轮的功能，所以抽取了WheelStateChangeListener
        binding.wheel.setOnWheelStateChangeListener(PitchWheelWheelStateChangeListener(activityViewModel))

        padViewModel.padSettings.observe(viewLifecycleOwner) { padSettings ->
            binding.padContainer.adapter = PadContainerAdapter(AKaiMPD218PadThemeInitializer()) { padId, state ->
                if (padViewModel.settingMode) {
                    // 只有当Pad抬起(STATE_OFF)时才跳转到设置界面
                    if (state == MidiMessage.STATE_ON) return@PadContainerAdapter
                    // to setting fragment
                    val action = PadFragmentDirections.actionPadFragmentToPadSettingFragment(padId)
                    findNavController().navigate(action)
                    padViewModel.closeSettingMode()
                } else {
                    // send midi message
                    activityViewModel.sendMessageToServer(
                        padViewModel.getMessageByPadState(padId, state, padSettings[padId - 1], buttonGroupAdapter.getBpm())
                    )
                }
            }
        }

    }

    companion object {
        const val TAG = "PadFragment"
    }

}