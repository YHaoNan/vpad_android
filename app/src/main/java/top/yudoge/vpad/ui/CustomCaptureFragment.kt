package top.yudoge.vpad.ui

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.king.zxing.CaptureFragment

class CustomCaptureFragment : CaptureFragment() {
    override fun initCameraScan() {
        super.initCameraScan()
        cameraScan.setVibrate(true)
            .setPlayBeep(true)
            .setOnScanResultCallback {
                cameraScan.setAnalyzeImage(false)
                setFragmentResult(QRSCAN_REQUEST_KEY, bundleOf(QRSCAN_BUNDLE_KEY to it.text))
                findNavController().popBackStack()
                true
            }
    }

    companion object {
        const val QRSCAN_REQUEST_KEY = "QRSCAN_RK"
        const val QRSCAN_BUNDLE_KEY = "QRSCAN_BK"
    }
}