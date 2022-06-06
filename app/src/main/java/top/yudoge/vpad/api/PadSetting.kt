package top.yudoge.vpad.api

import top.yudoge.vpad.toplevel.requireInRange

data class PadSetting(
    val mode: PadMode,
    val velocity: Int,
    val specificModeSetting: SpecificModeSetting
) {
    init {
        velocity.requireInRange(1, 127)
    }
}