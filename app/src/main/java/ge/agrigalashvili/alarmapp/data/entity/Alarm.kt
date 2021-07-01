package ge.agrigalashvili.alarmapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm(
    @ColumnInfo(name = "time_set") val timeSet: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}