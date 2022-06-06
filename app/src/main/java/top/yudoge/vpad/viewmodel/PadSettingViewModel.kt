package top.yudoge.vpad.viewmodel

import android.text.InputType
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.domain.PadSettingDomain
import top.yudoge.vpad.view.setting_view.SettingItem
import javax.inject.Inject


@HiltViewModel
class PadSettingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val padSettingDomain: PadSettingDomain,
): ViewModel() {
    val padId = savedStateHandle.get<Int>(PAD_ID_KEY)!!
    val settingItems: LiveData<List<SettingItem>> = padSettingDomain.getSettingItems(padId - 1)

    fun updateSettingItem(newItem: SettingItem) = viewModelScope.launch {
        padSettingDomain.updatePadSettingItem(padId - 1, newItem)
    }

    companion object {
        const val PAD_ID_KEY = "padId"
    }
}