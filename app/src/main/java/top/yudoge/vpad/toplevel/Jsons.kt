package top.yudoge.vpad.toplevel

import com.google.gson.*
import com.google.gson.internal.bind.TypeAdapters
import top.yudoge.vpad.api.*
import top.yudoge.vpad.pojo.*
import java.lang.IllegalStateException
import java.lang.reflect.Type

fun <T> JsonElement.to(clazz: Class<T>): T {
    return Gson().fromJson(this, clazz)
}

class SpecificModeSettingSerializer : JsonSerializer<SpecificModeSetting> {
    override fun serialize(
        src: SpecificModeSetting,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ) = when (src.mode) {
            PadMode.Pad ->    gson.toJsonTree(src, PadModeSetting::class.java)
            PadMode.Arp ->    gson.toJsonTree(src, ArpModeSetting::class.java)
            PadMode.Repeat -> gson.toJsonTree(src, RepeatModeSetting::class.java)
            PadMode.Chord ->  gson.toJsonTree(src, ChordModeSetting::class.java)
        }
}

class SpecificModeSettingDeserializer : JsonDeserializer<SpecificModeSetting> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SpecificModeSetting {
        val obj = json.asJsonObject
        val mode = obj.get("mode").asString
        obj.remove("mode")

        return when (mode) {
            PadMode.Pad.name -> PadModeSetting()
            PadMode.Arp.name -> obj.to(ArpModeSetting::class.java)
            PadMode.Chord.name -> obj.to(ChordModeSetting::class.java)
            PadMode.Repeat.name -> obj.to(RepeatModeSetting::class.java)
            else -> throw IllegalStateException("Unknown pad mode ${mode}")
        }

    }

}

inline fun JsonObject.replace(name: String, value: Any) {
    remove(name)
    if (value is Number) addProperty(name, value)
    else if (value is Char) addProperty(name, value)
    else if (value is Boolean) addProperty(name, value)
    else if (value is String) addProperty(name, value)
    else if (value is JsonElement) add(name, value)
    else add(name, gson.toJsonTree(value))
}
val gson = GsonBuilder()
    .registerTypeAdapter(SpecificModeSetting::class.java, SpecificModeSettingDeserializer())
    .registerTypeAdapter(SpecificModeSetting::class.java, SpecificModeSettingSerializer())
    .create()