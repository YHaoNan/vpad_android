package top.yudoge.vpad.api

import com.google.gson.annotations.SerializedName

enum class ChordType {
    @SerializedName("MAJOR")  MAJOR,
    @SerializedName("MINOR")  MINOR,
    @SerializedName("DOM")    DOM,
    @SerializedName("AUG")    AUG,
    @SerializedName("DIM")    DIM,
    @SerializedName("SUS2")   SUS2,
    @SerializedName("SUS4")   SUS4,
    @SerializedName("ADD6")   ADD6,
    @SerializedName("ADD9")   ADD9
}

enum class ChordLevel {
    @SerializedName("L_3")  L_3,
    @SerializedName("L_7")  L_7,
    @SerializedName("L_9")  L_9,
    @SerializedName("L_11") L_11,
    @SerializedName("L_13") L_13
}