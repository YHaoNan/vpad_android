package top.yudoge.vpad.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.yudoge.vpad.api.VPadServerScannedListener
import top.yudoge.vpad.api.VPadServerScanner
import javax.inject.Inject

@HiltViewModel
class ChooseVpadServerFragmentViewModel @Inject constructor(
    private val vPadServerScanner: VPadServerScanner
) : ViewModel() {

    fun scanVPadServer(lifecycleOwner: LifecycleOwner, serverScannedListener: VPadServerScannedListener) =
        viewModelScope.launch {
            vPadServerScanner.scanVPadServer(lifecycleOwner, serverScannedListener)
        }

}