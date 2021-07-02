package ge.agrigalashvili.alarmapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ge.agrigalashvili.alarmapp.data.entity.Alarm

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm")
    fun getAlarms() : List<Alarm>

    @Insert
    fun addAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)
}