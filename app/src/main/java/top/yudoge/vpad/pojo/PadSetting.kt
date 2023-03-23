package top.yudoge.vpad.pojo

import top.yudoge.vpad.api.PadMode
import top.yudoge.vpad.toplevel.requireInRange

data class PadSetting(
    val offset: Int,
    val title: String,
    val mode: PadMode,
    val velocity: Int,
    val specificModeSetting: SpecificModeSetting
) {
    init {
        velocity.requireInRange(1, 127)
    }
}