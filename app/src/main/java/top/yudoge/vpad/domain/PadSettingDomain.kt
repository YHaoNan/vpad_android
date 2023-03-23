package top.yudoge.vpad.domain

import android.text.InputType
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.gson.*
import top.yudoge.vpad.api.*
import top.yudoge.vpad.pojo.*
import top.yudoge.vpad.toplevel.gson
import top.yudoge.vpad.toplevel.names
import top.yudoge.vpad.view.setting_view.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PadSettingDomain @Inject constructor(
    private val presetDomain: PresetDomain
) {

    val padSettings: LiveData<List<PadSetting>> = Transformations.map(presetDomain.workingPreset) { preset ->
        preset.padSettings
    }

    private fun getPadSettingsFromJson(json: String): List<PadSetting> {
        return gson.fromJson(json, Array<PadSetting>::class.java).toList()
    }

    fun getPadSetting(padIdStartByZero: Int): LiveData<PadSetting> {
        return Transformations.map(padSettings) {
            it[padIdStartByZero]
        }
    }

    suspend fun updatePadSettingItem(index: Int, settingItem: SettingItem) {
        val workingPresetJsonString = presetDomain.workingPresetJson.value!!;
        val fullPresetJO = JsonParser.parseString(workingPresetJsonString).asJsonObject
        val padSettingJA = fullPresetJO["padSettings"].asJsonArray
        val jo = padSettingJA[index].asJsonObject



        when(settingItem.id) {
            VELOCITY ->  jo.replace("velocity", settingItem.inputValue().toInt())
            TITLE ->  jo.replace("title", settingItem.inputValue())
            OFFSET ->  jo.replace("offset", settingItem.inputValue().toInt())
            MODE -> {
                val padMode = PadMode.valueOf(settingItem.selectedValue())
                jo.replace("mode", padMode)
                jo.replace("specificModeSetting", when(padMode) {
                    PadMode.Arp -> ArpModeSetting()
                    PadMode.Chord -> ChordModeSetting()
                    PadMode.Repeat -> RepeatModeSetting()
                    PadMode.Pad -> PadModeSetting()
                })
            }
            ARP_METHOD, ARP_RATE, ARP_SWING_PCT, ARP_UP_NOTE_CNT, ARP_DYNAMIC_PCT, ARP_VELOCITY_AUTOMATION, CHORD_TYPE, CHORD_LEVEL, CHORD_ARP_PCT, CHORD_TRANSPOSE-> {
                val subSettingJo = jo.getAsJsonObject("specificModeSetting")
                when(settingItem.id) {
                    ARP_METHOD -> subSettingJo.replace("method", ArpMethod.valueOf(settingItem.selectedValue()))
                    ARP_RATE -> subSettingJo.replace("rate", ArpRate.valueOf(settingItem.selectedValue()))
                    ARP_SWING_PCT -> subSettingJo.replace("swingPct", settingItem.inputValue().toInt())
                    ARP_UP_NOTE_CNT -> subSettingJo.replace("upNoteCnt", settingItem.inputValue().toInt())
                    ARP_VELOCITY_AUTOMATION -> subSettingJo.replace("velocityAutomation", ArpVelocityAutomation.valueOf(settingItem.selectedValue()))
                    ARP_DYNAMIC_PCT -> subSettingJo.replace("dynamicPct", settingItem.inputValue().toInt())
                    CHORD_LEVEL -> subSettingJo.replace("level", ChordLevel.valueOf(settingItem.selectedValue()))
                    CHORD_TYPE -> subSettingJo.replace("type", ChordType.valueOf(settingItem.selectedValue()))
                    CHORD_ARP_PCT -> subSettingJo.replace("arpPct", settingItem.inputValue().toString())
                    CHORD_TRANSPOSE -> subSettingJo.replace("transpose", settingItem.inputValue().toInt())
                }
                jo.replace("specificModeSetting", subSettingJo)
            }
        }


        padSettingJA[index] = jo;
        fullPresetJO.replace("padSettings", padSettingJA)
        presetDomain.updateWorkingPreset(gson.toJson(fullPresetJO))

    }

    fun getSettingItems(padIdStartByZero: Int): LiveData<List<SettingItem>> =
        Transformations.map(getPadSetting(padIdStartByZero)) {
            mutableListOf<SettingItem>().apply {
                add("Pad ${padIdStartByZero + 1} 设置".asDivider())
                add(it.title.asInputItem(TITLE, "Pad标题", null, "Pad标题"))
                add(it.offset.asInputItem(OFFSET, "Pad Offset", null, "Pad Offset"))
                add(it.mode.asSelectItem(MODE, "Pad模式", "Pad触发后的事件模式"))
                add(it.velocity.asInputItem(VELOCITY, "力度", "Pad按下后的音符力度", "1~127"))

                val subSetting = it.specificModeSetting
                when (it.mode) {
                    PadMode.Arp -> {
                        subSetting as ArpModeSetting
                        add("Arp设置".asDivider())
                        add(subSetting.method.asSelectItem(ARP_METHOD, "琶音方式", "按照何种方式琶音"))
                        add(subSetting.rate.asSelectItem(ARP_RATE, "琶音频率", "按照何种频率进行琶音"))
                        add(subSetting.swingPct.asInputAndButtonItem(ARP_SWING_PCT, "swing", "琶音的摇摆程度", "0~100"))
                        add(subSetting.upNoteCnt.asInputAndButtonItem(ARP_UP_NOTE_CNT, "不同音符数", "按照琶音方式产生的不同音符个数", "1~8", step2Show = false))
                        add(subSetting.velocityAutomation.asSelectItem(ARP_VELOCITY_AUTOMATION, "力度包络", "琶音音符的力度自动化"))
                        add(subSetting.dynamicPct.asInputAndButtonItem(ARP_DYNAMIC_PCT, "动态范围", "力度包络遵循的动态范围", "0~200"))
                    }
                    PadMode.Repeat -> {
                        subSetting as RepeatModeSetting
                        add("Repeat设置".asDivider())
                        add(subSetting.rate.asSelectItem(ARP_RATE, "频率", "按照何种频率进行重复"))
                        add(subSetting.swingPct.asInputAndButtonItem(ARP_SWING_PCT, "swing", "重复的摇摆程度", "0~100"))
                        add(subSetting.velocityAutomation.asSelectItem(ARP_VELOCITY_AUTOMATION, "力度包络", "重复音符的力度自动化"))
                        add(subSetting.dynamicPct.asInputAndButtonItem(ARP_DYNAMIC_PCT, "动态范围", "力度包络遵循的动态范围", "0~200"))
                    }
                    PadMode.Chord -> {
                        subSetting as ChordModeSetting
                        add("Chord设置".asDivider())
                        add(subSetting.type.asSelectItem(CHORD_TYPE, "和弦类别", null))
                        add(subSetting.level.asSelectItem(CHORD_LEVEL, "和弦级数", null))
                        add(subSetting.arpPct.asInputAndButtonItem(CHORD_ARP_PCT, "琶音程度", "控制和弦中不同音的起始延迟", "0~100"))
                        add(subSetting.transpose.asInputItem(CHORD_TRANSPOSE, "和弦转置", "控制和弦的转置", ""))
                    }
                }
            }
        }

    companion object {
        private const val NO_ID = -1
        private const val VELOCITY = 0
        private const val MODE = 1
        private const val ARP_METHOD = 2
        private const val ARP_RATE = 3
        private const val ARP_SWING_PCT = 4
        private const val ARP_UP_NOTE_CNT = 5
        private const val ARP_VELOCITY_AUTOMATION = 6
        private const val ARP_DYNAMIC_PCT = 7
        private const val CHORD_TYPE = 8
        private const val CHORD_LEVEL = 9
        private const val CHORD_ARP_PCT = 10
        private const val CHORD_TRANSPOSE = 11
        private const val TITLE = 13
        private const val OFFSET = 14
        const val TAG = "PadSettingDomain"

    }

    private inline fun String.asDivider(id: Int = NO_ID) = SettingDivider(id, this)
    private inline fun <reified T : Enum<*>> T.asSelectItem(id: Int = NO_ID, title: String, subTitle: String?)  =
        SelectSettingItem(id, title, subTitle, ordinal, enumValues<T>().names())
    private inline fun Int.asInputItem(id: Int = NO_ID, title: String, subTitle: String?, hint: String) =
        InputSettingItem(id, title, subTitle, this.toString(), hint, InputType.TYPE_CLASS_NUMBER)
    private inline fun String.asInputItem(id: Int = NO_ID, title: String, subTitle: String?, hint: String) =
        InputSettingItem(id, title, subTitle, this, hint, InputType.TYPE_CLASS_TEXT)
    private inline fun Int.asInputAndButtonItem(id: Int = NO_ID, title: String, subTitle: String?, hint: String, step2Show: Boolean = true) =
        InputAndButtonSettingItem(id, title, subTitle, this.toString(), hint, InputType.TYPE_CLASS_NUMBER, step2Show = step2Show)
    private inline fun SettingItem.inputValue(): String {
        return (this as InputSettingItem).value
    }
    private inline fun SettingItem.selectedValue(): String {
        this as SelectSettingItem
        return this.values[this.valueId]
    }
    private inline fun JsonObject.replace(name: String, value: Any) {
        remove(name)
        if (value is Number) addProperty(name, value)
        else if (value is Char) addProperty(name, value)
        else if (value is Boolean) addProperty(name, value)
        else if (value is String) addProperty(name, value)
        else if (value is JsonElement) add(name, value)
        else add(name, gson.toJsonTree(value))
    }

}