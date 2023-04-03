package top.yudoge.vpad.api

import com.google.gson.annotations.SerializedName


enum class ArpMethod {
    @SerializedName("NO_METHOD")   NO_METHOD,
    @SerializedName("UP")          UP,
    @SerializedName("DOWN")        DOWN,
    @SerializedName("UP_DOWN")     UP_DOWN,
    @SerializedName("DOWN_UP")     DOWN_UP,
    @SerializedName("THREE_CHORD") THREE_CHORD,
    @SerializedName("SEVEN_CHORD") SEVEN_CHORD;
}

enum class ArpRate(val rateLabel: String) {
    @SerializedName("R_1_1")     R_1_1("1/1"),
    @SerializedName("R_1_2_D")   R_1_2_D("1/2D"),
    @SerializedName("R_1_1_T")   R_1_1_T("1/1T"),
    @SerializedName("R_1_2")     R_1_2("1/2"),
    @SerializedName("R_1_4_D")   R_1_4_D("1/4D"),
    @SerializedName("R_1_2_T") R_1_2_T("1/2T"),
    @SerializedName("R_1_4") R_1_4("1/4"),
    @SerializedName("R_1_8_D")R_1_8_D("1/8D"),
    @SerializedName("R_1_4_T")R_1_4_T("1/4T"),
    @SerializedName("R_1_8")R_1_8("1/8"),
    @SerializedName("R_1_16_D")R_1_16_D("1/16D"),
    @SerializedName("R_1_8_T")R_1_8_T("1/8T"),
    @SerializedName("R_1_16")R_1_16("1/16"),
    @SerializedName("R_1_32_D")R_1_32_D("1/32D"),
    @SerializedName("R_1_16_T")R_1_16_T("1/16T"),
    @SerializedName("R_1_32")R_1_32("1/32"),
    @SerializedName("R_1_64D")R_1_64D("1/64"),
    @SerializedName("R_1_32_T")R_1_32_T("1/32T"),
    @SerializedName("R_1_64")R_1_64("1/64"),
    @SerializedName("R_1_64_T")R_1_64_T("1/64T");
}

enum class ArpVelocityAutomation {
    @SerializedName("NO_AUTOMATION") NO_AUTOMATION,
    @SerializedName("UP") UP,
    @SerializedName("DOWN") DOWN,
    @SerializedName("UP_DOWN") UP_DOWN,
    @SerializedName("DOWN_UP") DOWN_UP,
    @SerializedName("STEP") STEP,
    @SerializedName("RANDOM") RANDOM
}
