package top.yudoge.vpad.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import top.yudoge.vpad.pojo.Preset
import top.yudoge.vpad.repository.SettingRepository
import top.yudoge.vpad.toplevel.gson
import javax.inject.Inject
import javax.inject.Singleton
import top.yudoge.vpad.toplevel.replace

@Singleton
class WorkingPresetDomain @Inject constructor(
    private val settingRepository: SettingRepository
){
    val workingPresetJson: LiveData<String> = settingRepository.workingPreset.asLiveData()
    // convert LiveData<PresetJsonString> => LiveData<Preset>
    val workingPreset: LiveData<Preset> = Transformations.map(workingPresetJson) {
        gson.fromJson(it, Preset::class.java)
    }

    // 只允许外界通过Json串来修改Preset，发布给应用的Preset是完全不可变的，应用不可以修改其中的任何数据
    suspend fun updateWorkingPreset(presetJson: String) {
        Log.i("WorkingPresetDomain", presetJson)
        settingRepository.updateWorkingPreset(presetJson)
    }

    // 一个模板方法，将当前workingPreset转成JsonObject，调用updateStrategy，传入当前的JsonObject
    // updateStrategy分析当前的JsonObject，判断是否需要修改，比如如果在更新padsPerLine时，新值和旧值一样
    // 就无需更改。如果需要更改，执行更改。updateStrategy的返回值代表是否需要实际执行更改。
    private suspend fun updateInJsonFormat(updateStrategy: (jo: JsonObject) -> Boolean) {
        val workingPresetJsonString = this.workingPresetJson.value!!;
        val fullPresetJO = JsonParser.parseString(workingPresetJsonString).asJsonObject
        if (updateStrategy.invoke(fullPresetJO))
            updateWorkingPreset(gson.toJson(fullPresetJO))
    }

    suspend fun updatePadsPerLine(newValue: Int) = updateInJsonFormat {
        if (it["padsPerLine"].asInt != newValue) {
            it.replace("padsPerLine", newValue)
            true
        }
        else false
    }

    suspend fun updateRegionSpan(newValue: Int) = updateInJsonFormat {
        if (it["regionSpan"].asInt != newValue) {
            it.replace("regionSpan", newValue)
            true
        }
        else false
    }

    /**
     * 在原来的基础上修改baseNote，baseNote += incr
     * incr可以为负数，不过有如下限制，如果当前最大的offset + 修改后的baseNote > 127，则修改失败
     *                           如果当前最小的offset + 修改后的baseNote < 0，则修改失败
     *                           (注意，在修改一个pad的offset设置时，应该也要判断offset+当前baseNote是否合法，这里我们没有做相应判断)
     * 该方法返回incr，若未进行修改，则返回0
     */
    private suspend fun _increaseBaseNote(incr: Int): Int {

        val curr = workingPreset.value!!.baseNote // 当前baseNote

        val workingPresetJsonString = this.workingPresetJson.value!!;
        val fullPresetJO = JsonParser.parseString(workingPresetJsonString).asJsonObject

        val next = curr + incr // 更新后的baseNote
        val maxOffset = workingPreset.value!!.padSettings.maxOf { st -> st.offset } // 当前最大offset
        val minOffset = workingPreset.value!!.padSettings.minOf { st -> st.offset } // 当前最小offset

        // 若当前最大offset+baseNote已经大于127，或者baseNote已经小于0，则不同意本次increase
        if (next + maxOffset > 127 || next + minOffset < 0) {
            return 0
        }

        fullPresetJO.replace("baseNote", next)
        updateWorkingPreset(gson.toJson(fullPresetJO))

        return incr
    }

    suspend fun increaseBaseNote(): Int {
        val rgnSpan = workingPreset.value!!.regionSpan
        return _increaseBaseNote(rgnSpan)
    }

    suspend fun decreaseBaseNote(): Int {
        val rgnSpan = workingPreset.value!!.regionSpan
        return _increaseBaseNote(-rgnSpan)
    }



}