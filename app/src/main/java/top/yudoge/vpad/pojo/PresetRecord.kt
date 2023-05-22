package top.yudoge.vpad.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * PresetRecord代表一个Preset的数据库记录
 * @property id             和业务无关的一个ID
 * @property presetName     预设名
 * @property author         作者名
 * @property description    预设描述
 * @property targetFile     预设文件，只保存文件名，统一存储在预设目录下
 * @property createTime     预设创建时间
 */
@Entity(tableName = "tb_preset_records", indices = [Index(value = ["preset_name"], unique = true)])
data class PresetRecord(
    @PrimaryKey(autoGenerate = true)    val id: Int,
    @ColumnInfo(name = "preset_name")   val presetName: String,
    @ColumnInfo(name = "author")        val author: String,
    @ColumnInfo(name = "description")   val description: String,
    @ColumnInfo(name = "target_file")   val targetFile: String,
    @ColumnInfo(name = "create_time")   val createTime: Long
) {
    /**
     * 从presetRecord生成一个Preset，所有需要读preset文件的字段都会被设置成0值
     */
    fun toPresetWithoutReadPresetFileFile() = Preset(presetName, author, description, 0, 0, 0, 1, listOf())
}