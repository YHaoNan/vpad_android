package top.yudoge.vpad.ui

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import top.yudoge.vpad.BuildConfig
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpad.toplevel.share
import top.yudoge.vpad.toplevel.showInputDialog
import top.yudoge.vpad.toplevel.showMessageDialog
import top.yudoge.vpad.viewmodel.PresetViewModel
import java.io.File

@AndroidEntryPoint
class PresetFragment : Fragment() {
    private val viewModel: PresetViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        viewModel.flushPresets()
        setContent {
            val presets =  viewModel.preset.observeAsState()
            MaterialTheme {
                Surface {
                    presets.value?.let {
                        LazyColumn(Modifier.padding(10.dp)) {
                            items(it, key = {item -> item.presetName}) {
                                PresetItem(preset = it, Modifier.clickable { // 点击弹出详情
                                    context.showMessageDialog("${it.presetName} by ${it.author}", it.description)
                                }) {
                                    // 选中该preset的回调
                                    context.showMessageDialog("确定", "确定要加载${it.presetName}作为当前的工作预制吗？", okCallback = {iface, i ->
                                        viewModel.setWorkingPreset(it)
                                        iface.dismiss()
                                        Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show()
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun PresetItem(preset: Preset, modifier: Modifier = Modifier, onChoose: () -> Unit) {
        SwipeToDismiss(
            state = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        requireActivity().showMessageDialog("确认删除", "删除后不可恢复，确认要删除吗?", okCallback = {di, i ->
                            viewModel.deletePreset(preset)
                            di.dismiss();
                        })
                    } else if (it == DismissValue.DismissedToEnd) {
                        val file = File(File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), Constants.PRESET_DIR), preset.presetName + ".preset.json")
                        requireActivity().share(FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID+".fileprovider", file))
                    }
                    false
                }
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp, 5.dp),
            background = {
                SwipeBackground()
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(MaterialTheme.colors.background)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = preset.presetName, fontWeight = FontWeight.Bold)
                    Text(text = preset.description, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colors.secondary)
                }
                Spacer(modifier = Modifier
                    .width(0.5.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colors.secondary))
                Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Use", color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                        onChoose()
                    })
                }
            }
        }
    }

    @Composable
    fun SwipeBackground() {
        Row {
            Text(text = "Share", modifier = Modifier.align(Alignment.CenterVertically).weight(1f).fillMaxHeight().background(Color.Green).padding(start = 10.dp), color = Color.Black)
            Text(text = "Delete", textAlign = TextAlign.End, modifier = Modifier.align(Alignment.CenterVertically).weight(1f).fillMaxHeight().background(Color.Red).padding(end = 10.dp), color = Color.White)
        }
    }

}