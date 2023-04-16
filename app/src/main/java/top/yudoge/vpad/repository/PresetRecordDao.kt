package top.yudoge.vpad.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import top.yudoge.vpad.pojo.PresetRecord

/**
 * 操作PresetRecord的DAO对象，用户（甚至ViewModel）不应该直接访问该对象，而是使用PresetDomain
 */
@Dao
interface PresetRecordDao {
    @Query("SELECT * FROM tb_preset_records WHERE preset_name LIKE '%' || :subInName || '%' ORDER BY preset_name")
    fun getAllOrderByName(subInName: String): Flow<List<PresetRecord>>

    @Query("SELECT * FROM tb_preset_records WHERE preset_name LIKE '%' || :subInName || '%' ORDER BY create_time")
    fun getAllOrderByCreateTime(subInName: String): Flow<List<PresetRecord>>

    @Delete
    fun delete(presetRecord: PresetRecord)

    @Insert
    fun insert(presetRecord: PresetRecord)
}