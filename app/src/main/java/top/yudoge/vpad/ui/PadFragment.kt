package top.yudoge.vpad.ui

import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import top.yudoge.vpad.BuildConfig
import top.yudoge.vpad.adapter.ButtonGroupAdapter
import top.yudoge.vpad.adapter.PadContainerAdapter
import top.yudoge.vpad.databinding.FragmentPadBinding
import top.yudoge.vpad.di.Pixel
import top.yudoge.vpad.padtheme.AKaiMPD218PadThemeInitializer
import top.yudoge.vpad.pojo.ButtonLabel
import top.yudoge.vpad.toplevel.*
import top.yudoge.vpad.view.PitchWheelWheelStateChangeListener
import top.yudoge.vpad.view.UnscrollableRecyclerView.OnDragStateChangedListener
import top.yudoge.vpad.viewmodel.MainViewModel
import top.yudoge.vpad.viewmodel.PadViewModel
import top.yudoge.vpadapi.structure.MidiMessage
import javax.inject.Inject


@AndroidEntryPoint
class PadFragment : Fragment() {

    private lateinit var vibrator: Vibrator
    private val padViewModel: PadViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentPadBinding
    lateinit var buttonGroupAdapter: ButtonGroupAdapter

    @Inject @Pixel lateinit var pixelTypeface: Typeface

    private val buttonGroupClickListener: (ButtonLabel) -> Unit = {
        when(it.label) {
            "Rgn+" -> padViewModel.increaseNoteRegion()
            "Rgn-" -> padViewModel.decreaseNoteRegion()
            "PDST" -> padViewModel.toggleSettingMode()
            "BPM" -> context?.showInputDialog("设置BPM", value = buttonGroupAdapter.getBpm().toString(), null) {
                padViewModel.setBpm(it.toInt())
            }
            "PLAY", "STOP", "REC", "LOOP", "UNDO", "REDO", "CLK", "SAVE" -> {
                padViewModel.getControlMessageByLabel(it.label)?.let {
                    activityViewModel.sendMessageToServer(it)
                }
            }
            "Pst" -> gotoPresetUI()
            "Col" -> context?.showInputDialog("设置一行pad数", value = padViewModel.workingPreset.value!!.padsPerLine.toString(), null) {
                padViewModel.setPadsPerLine(it.toInt())
            }
            "Exp" -> {
                context?.showInputDialog("为你的Preset取一个名字", padViewModel.workingPreset.value?.presetName ?: "", null, InputType.TYPE_CLASS_TEXT) {presetName ->
                    context?.showInputDialog("留下你的名字", padViewModel.workingPreset.value?.author ?: "", null, InputType.TYPE_CLASS_TEXT) {authorName ->
                        context?.showInputDialog("预设说明", padViewModel.workingPreset.value?.description ?: "", null, InputType.TYPE_CLASS_TEXT) {description ->
                            val exportedFile = padViewModel.exportWorkingPreset(presetName, authorName, description = description)
                            requireActivity().share(FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID+".fileprovider", exportedFile))
                        }
                    }
                }
            }
        }
    }

    private fun gotoPresetUI() {
        val action = PadFragmentDirections.actionPadFragmentToPresetFragment()
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 竖屏
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
        binding.addPad.setOnClickListener {
            padViewModel.appendPad()
        }

        padViewModel.settingMode.observe(viewLifecycleOwner) {
            if (binding.padContainer.adapter == null) return@observe
            val adapter = binding.padContainer.adapter as PadContainerAdapter
            // 设置draggable
            if (it) {
                binding.padContainer.enableDraggable()
                adapter.enableSettingMode()
            }
            else {
                binding.padContainer.disableDraggable()
                adapter.disableSettingMode()
            }
        }

        val padThemeInitializer = AKaiMPD218PadThemeInitializer();

        binding.padContainer.setOnDragStateChangedListener(object : OnDragStateChangedListener {
            override fun onDragStart(dragItem: View, dragItemPosition: Int) {
                padViewModel.currentDragItem = dragItemPosition;
                vibrator.vibrate(50)
            }

            override fun onMoveInAnItem(
                dragItem: View,
                dragItemPosition: Int,
                moveInItem: View,
                moveInItemPosition: Int
            ) {
                Log.i("PadFragment", "MoveInAnItem: " + dragItemPosition + " | " + moveInItemPosition);
            }

            override fun onMoveOutAnItem(
                dragItem: View,
                dragItemPosition: Int,
                moveOutItem: View,
                moveOutItemPosition: Int
            ) {
                Log.i("PadFragment", "MoveOutAnItem: " + dragItemPosition + " | " + moveOutItemPosition);
            }

            override fun onHoverOnAnItem(
                dragItem: View,
                dragItemPosition: Int,
                hoverOnItem: View,
                hoverOnItemPosition: Int
            ) {
                vibrator.vibrate(50)
                if (padViewModel.containsCopyPad(hoverOnItemPosition, hoverOnItem)) {
                    padThemeInitializer.turnOffPadViewManually(hoverOnItem)
                    padViewModel.removeCopyPad(hoverOnItemPosition, hoverOnItem)
                } else {
                    padThemeInitializer.turnOnPadViewManually(hoverOnItem)
                    padViewModel.addCopyPad(hoverOnItemPosition, hoverOnItem)
                }
            }

            override fun onDragRelease(
                dragItem: View,
                dragItemPosition: Int,
                releaseOnItem: View?,
                releaseOnItemPosition: Int
            ) {
                if (padViewModel.copyToPadIndexies.size <= 0) return
                requireActivity().alert("是否要复制设置", "是否要将设置复制给选定的pad", posiButtonLabel = "确定", posiButtonCallback = {di, i ->
                    padViewModel.updatePadSettingsBatch()
                    pubBehavior(di)
                }, negButtonLabel = "取消", negButtonCallback = {di, i ->
                    pubBehavior(di)
                }).show()
            }
            fun pubBehavior(di: DialogInterface) {
                di.dismiss()
                padViewModel.copyToPadIndexies.forEach {
                    padThemeInitializer.turnOffPadViewManually(it.second)
                }
                padViewModel.currentDragItem = -1;
                padViewModel.clearCopyPads()
                padViewModel.closeSettingMode()
            }
        });

        padViewModel.workingPreset.observe(viewLifecycleOwner) { preset ->
            val girdLayoutManager = GridLayoutManager(activity, preset.padsPerLine);
            binding.padContainer.layoutManager = girdLayoutManager
            binding.padContainer.adapter = PadContainerAdapter(padThemeInitializer, preset.padSettings, padViewModel.settingMode.value!!) { padSetting, padPosition, event ->
                // 只有当没有进行padSetting复制时才跳转到设置页面
                if (event == PadEvent.OpenSetting && padViewModel.copyToPadIndexies.size != 0) {
                    // to setting fragment
                    val action = PadFragmentDirections.actionPadFragmentToPadSettingFragment(padPosition)
                    findNavController().navigate(action)
                    padViewModel.closeSettingMode()
                } else if (event == PadEvent.Down){
                    // send midi message
                    activityViewModel.sendMessageToServer(
                        padViewModel.getMessageByPadState(1, padSetting, buttonGroupAdapter.getBpm(), preset.baseNote)
                    )
                } else if (event == PadEvent.Release){
                    // send midi message
                    activityViewModel.sendMessageToServer(
                        padViewModel.getMessageByPadState(0, padSetting, buttonGroupAdapter.getBpm(), preset.baseNote)
                    )
                } else if (event == PadEvent.Delete) {
                    padViewModel.deletePadAt(padPosition)
                }
            }
        }
    }

    companion object {
        const val TAG = "PadFragment"
    }

}