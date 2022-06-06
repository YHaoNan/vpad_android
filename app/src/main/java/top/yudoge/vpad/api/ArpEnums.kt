package top.yudoge.vpad.api


enum class ArpMethod {
    NO_METHOD,
    UP,
    DOWN,
    UP_DOWN,
    DOWN_UP,
    THREE_CHORD,
    SEVEN_CHORD;
}

enum class ArpRate(val rateLabel: String) {
    R_1_1("1/1"),
    R_1_2_D("1/2D"),
    R_1_1_T("1/1T"),
    R_1_2("1/2"),
    R_1_4_D("1/4D"),
    R_1_2_T("1/2T"),
    R_1_4("1/4"),
    R_1_8_D("1/8D"),
    R_1_4_T("1/4T"),
    R_1_8("1/8"),
    R_1_16_D("1/16D"),
    R_1_8_T("1/8T"),
    R_1_16("1/16"),
    R_1_32_D("1/32D"),
    R_1_16_T("1/16T"),
    R_1_32("1/32"),
    R_1_64D("1/64"),
    R_1_32_T("1/32T"),
    R_1_64("1/64"),
    R_1_64_T("1/64T");
}

enum class ArpVelocityAutomation {
    NO_AUTOMATION,
    UP,
    DOWN,
    UP_DOWN,
    DOWN_UP,
    STEP,
    RANDOM
}
