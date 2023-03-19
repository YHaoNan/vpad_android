package top.yudoge.vpad.api

import top.yudoge.vpad.toplevel.requireInRange

abstract class SpecificModeSetting(
    val mode: PadMode
)
class PadModeSetting : SpecificModeSetting(
    PadMode.Pad
) {
    override fun toString(): String {
        return "PadModeSetting()"
    }
}

open class RepeatOrArpModeSetting(
    val rate: ArpRate = ArpRate.R_1_8,
    val swingPct: Int = 0,
    val velocityAutomation: ArpVelocityAutomation = ArpVelocityAutomation.UP,
    val dynamicPct: Int = 20,
    mode: PadMode
) : SpecificModeSetting(mode) {
    init {
        swingPct.requireInRange(0, 100)
        dynamicPct.requireInRange(0, 200)
    }
}

class ArpModeSetting(
    val method: ArpMethod = ArpMethod.UP,
    rate: ArpRate = ArpRate.R_1_8,
    swingPct: Int = 0,
    val upNoteCnt: Int = 4,
    velocityAutomation: ArpVelocityAutomation = ArpVelocityAutomation.UP,
    dynamicPct: Int = 20,
) : RepeatOrArpModeSetting(
    rate, swingPct, velocityAutomation, dynamicPct, PadMode.Arp
) {

    init {
        upNoteCnt.requireInRange(1, 8)
    }

    override fun toString(): String {
        return "ArpModeSetting(method=$method, rate=$rate, swingPct=$swingPct, upNoteCnt=$upNoteCnt, velocityAutomation=$velocityAutomation, dynamicPct=$dynamicPct)"
    }


}

class RepeatModeSetting(
    rate: ArpRate = ArpRate.R_1_8,
    swingPct: Int = 0,
    velocityAutomation: ArpVelocityAutomation = ArpVelocityAutomation.UP,
    dynamicPct: Int = 20,
) : RepeatOrArpModeSetting(
    rate, swingPct, velocityAutomation, dynamicPct, PadMode.Repeat
) {

    override fun toString(): String {
        return "RepeatModeSetting(rate=$rate, swingPct=$swingPct, velocityAutomation=$velocityAutomation, dynamicPct=$dynamicPct)"
    }


}

class ChordModeSetting(
    val type: ChordType = ChordType.MAJOR,
    val level: ChordLevel = ChordLevel.L_7,
    val transpose: Int = 0,
    val arpPct: Int = 10
) : SpecificModeSetting(
    PadMode.Chord
) {

    init {
        arpPct.requireInRange(0, 100)
    }

    override fun toString(): String {
        return "ChordModeSetting(type=$type, level=$level, transpose=$transpose, arpPct=$arpPct)"
    }

}