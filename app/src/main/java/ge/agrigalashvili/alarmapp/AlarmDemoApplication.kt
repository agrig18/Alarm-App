package ge.agrigalashvili.alarmapp

import android.app.Application
import ge.agrigalashvili.alarmapp.data.DemoDatabase

class AlarmDemoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DemoDatabase.createDatabase(this)
    }
}