package top.yudoge.vpad.pojo

import top.yudoge.vpad.toplevel.myGetOrDefault

enum class FaderMode {
    Track, CC
}

private val CC_NAME_MAP = mapOf(
    0 to "Bank",
    1 to "Modulation",
    2 to "Breath",
    4 to "Foot",
    7 to "Volume",
    8 to "Balance",
    10 to "Pan",
    11 to "Expression",
    12 to "Effect1",
    13 to "Effect2",
    64 to "Sustain",
    65 to "Portamento"
)


/**
 * id ： 推子的序号，一共0~7
 * value ： 推子的值，0~127，代表响度或者CC值
 * mode ： 推子模式
 * map ： 推子映射到什么上，如果推子模式是Track，则map代表某一个轨道，如果推子模式是CC，则map代表一个CC通道
 */
data class Fader(
    val id: Int,
    val value: Int,
    val mode: FaderMode = FaderMode.Track,
    val map: Int,
    val label: String = if (mode == FaderMode.Track) "Track ${map}" else "CC ${map} (${CC_NAME_MAP.myGetOrDefault(map, "Unknown")})"
)