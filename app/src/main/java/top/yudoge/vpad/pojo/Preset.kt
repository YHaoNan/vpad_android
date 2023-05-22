package top.yudoge.vpad.pojo

import java.util.*

/**
 * 在VPad中，软件所展示的打击垫UI来源于一个Preset，也就是预制
 * Preset规定了当前的基音符，规定了用户调用Rgn+和Rgn-时，基音符
 * 的增加减少范围，规定了界面中一行有几个Pad，规定了每一个位置上的
 * Pad的详情
 *
 *
 * @property presetName        规定了当前预制的名字
 * @property author            规定了当前预制的作者
 * @property presetDescription 规定了当前预制的介绍
 * @property baseNote          规定了当前预制的基音符，每一个PadSetting都会有一个offset，它代表该Pad触发后的音符是baseNote + offset
 * @property regionSpan        规定了当前用户调用Rgn+和Rgn-时，baseNote偏移的范围，比如regionSpan=16，用户调用Rgn-，baseNote -= 16
 * @property padsPerLine       规定了一行有多少pad
 * @property padSettings       规定了每一个pad的详细设置
 *                             这里的列表中每一个项目代表UI上的一个Pad，从UI上按照从上到下，从左到右的顺序排布，每一行有padsPerLine个
 *
 * VPad的工作模式是从Json文件中读取Preset，将其设置为当前工作Preset，你可以在上面更改
 * baseNote、regionSpan、padsPerLine、padSettings等设置，更改后的设置会自动保存
 * 当你觉得经过你修改后，当前设置已经可以作为一个满足特定使用场景的新Preset时，你可以导出
 * 你当前的工作设置，并填写author、presetName、presetDescription
 *
 * 目前，为了保持简单，VPad的Preset文件只能是纯描述性的JSON，未来，VPad可能会让不同的Preset有机会修改UI样式，比如提供251和弦的Preset可能会
 * 模仿keyboard的样式，这可能需要Preset文件是一个包含资源文件和自描述文件的压缩包。
 */
data class Preset(
    val presetName: String,
    val author: String,
    val description: String,
    val baseNote: Int,
    val regionSpan: Int,
    val padsPerLine: Int,
    val channel: Int,
    val padSettings: List<PadSetting>
) {
    fun newPresetFromThis(presetName: String, author: String, description: String): Preset {
        return Preset(presetName, author, description, this.baseNote, this.regionSpan, this.padsPerLine, this.channel, this.padSettings)
    }

    fun toPresetRecord(targetFile: String) = PresetRecord(0, presetName, author, description, targetFile, Date().time)
}