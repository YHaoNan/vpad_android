package top.yudoge.vpad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.AndroidEntryPoint
import top.yudoge.vpad.BuildConfig
import top.yudoge.vpad.R
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.pojo.PresetRecord
import top.yudoge.vpad.toplevel.Constants
import top.yudoge.vpad.toplevel.gson
import top.yudoge.vpad.toplevel.share
import top.yudoge.vpad.toplevel.showMessageDialog
import top.yudoge.vpad.viewmodel.PresetViewModel
import java.io.File
import java.io.InputStreamReader


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
        setContent {
//            val presets =  viewModel.preset.observeAsState()
            var filter by remember { mutableStateOf("") }
            val presetRecords = viewModel.getAllPresetRecordOrderByName(filter).collectAsState(listOf())

            MaterialTheme {
                Surface {
                    presetRecords.value?.let {
                        Column {
                            TopBar(onSearch = {
                                filter = it
                            }, onImport = {
                                openFileChoosor()
                            })
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
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.apply {
                    try {
                        val content = InputStreamReader(requireContext().contentResolver.openInputStream(this)).readText()
                        viewModel.importPreset(gson.fromJson(content, Preset::class.java))
                    } catch (e: JsonSyntaxException) {
                        Toast.makeText(requireContext(), "貌似不是一个合法的preset文件", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )
    private fun openFileChoosor() {
        Intent().apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            setAction(Intent.ACTION_GET_CONTENT)
            setType("application/json")
            resultLauncher.launch(this)
        }
    }

    // 头部搜索框 + 导入按钮
    @Composable
    fun TopBar(onSearch: (String)->Unit, onImport: ()->Unit) {
        var text by remember { mutableStateOf("") }
        Row(modifier =  Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {onSearch(text)}),
                onValueChange = { text = it },
                label = { Text(text = "filter")},
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    Row {
                        IconButton(onClick = { text=""; onSearch(text) }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_clear_24), contentDescription = "")
                        }
                        TextButton(onClick = { onImport() }) {
                            Text(text = "导入")
                        }
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun PresetItem(preset: PresetRecord, modifier: Modifier = Modifier, onChoose: () -> Unit) {
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
            Text(text = "Share", modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .fillMaxHeight()
                .background(Color.Green)
                .padding(start = 10.dp), color = Color.Black)
            Text(text = "Delete", textAlign = TextAlign.End, modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .fillMaxHeight()
                .background(Color.Red)
                .padding(end = 10.dp), color = Color.White)
        }
    }

}