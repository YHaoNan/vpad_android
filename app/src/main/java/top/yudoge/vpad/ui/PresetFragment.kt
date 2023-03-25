package top.yudoge.vpad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.toplevel.showInputDialog
import top.yudoge.vpad.toplevel.showMessageDialog
import top.yudoge.vpad.viewmodel.PresetViewModel

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
                            items(it) {
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

    @Composable
    fun PresetItem(preset: Preset, modifier: Modifier = Modifier, onChoose: () -> Unit) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp, 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = preset.presetName, fontWeight = FontWeight.Bold)
                    Text(text = preset.description, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier
                    .width(0.5.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.secondary))
                Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Choose", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                        onChoose()
                    })
                }
            }
        }
    }

}