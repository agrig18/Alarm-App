package ge.agrigalashvili.alarmapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ge.agrigalashvili.alarmapp.data.dao.AlarmDao
import ge.agrigalashvili.alarmapp.data.entity.Alarm

@Database(entities = arrayOf(Alarm::class), version = 1)
abstract class DemoDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object{
        private const val dbName = "alarm-db"

        private lateinit var INSTANCE: DemoDatabase

        fun getInstance(): DemoDatabase{
            return INSTANCE
        }

        fun createDatabase(context: Context){
            INSTANCE = Room.databaseBuilder(
                context, DemoDatabase::class.java, dbName).build()
        }


    }
}