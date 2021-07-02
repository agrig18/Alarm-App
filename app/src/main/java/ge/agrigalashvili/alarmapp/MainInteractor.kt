package ge.agrigalashvili.alarmapp

import android.os.AsyncTask
import ge.agrigalashvili.alarmapp.data.DemoDatabase
import ge.agrigalashvili.alarmapp.data.entity.Alarm

class MainInteractor(val presenter: IMainPresenter) {
    fun getAlarmsListFromDatabase(){
        GetAlarmsTask(presenter).execute()
    }

    fun addAlarmToDatabase(alarm: Alarm){
        AddAlarmTask(presenter, alarm).execute()
    }

    fun deleteAlarmFromDatabase(alarm: Alarm){
        DeleteAlarmTask(presenter, alarm).execute()
    }

    class GetAlarmsTask(val presenter: IMainPresenter): AsyncTask<Void, Void, List<Alarm>>(){
        override fun doInBackground(vararg params: Void?): List<Alarm> {
            var alarmDao = DemoDatabase.getInstance().alarmDao()
//            alarmsDao.addAlarm(Alarm("13:05"))
            var list: List<Alarm> = alarmDao.getAlarms()
            return list
        }

        override fun onPostExecute(result: List<Alarm>?) {
            super.onPostExecute(result)
            if (result != null){
                presenter.onAlarmListFetched(result)
            }
        }

    }

    class AddAlarmTask(val presenter: IMainPresenter, val alarm: Alarm): AsyncTask<Void, Void, List<Alarm>>(){
        override fun doInBackground(vararg params: Void?): List<Alarm> {
            var alarmDao = DemoDatabase.getInstance().alarmDao()
            alarmDao.addAlarm(alarm)
            var list: List<Alarm> = alarmDao.getAlarms()
            return list
        }

        override fun onPostExecute(result: List<Alarm>?) {
            super.onPostExecute(result)
            if (result != null){
                presenter.onAlarmListFetched(result)
            }
        }

    }

    class DeleteAlarmTask(val presenter: IMainPresenter, val alarm: Alarm): AsyncTask<Void, Void, List<Alarm>>(){
        override fun doInBackground(vararg params: Void?): List<Alarm> {
            var alarmDao = DemoDatabase.getInstance().alarmDao()
            alarmDao.deleteAlarm(alarm)
            var list: List<Alarm> = alarmDao.getAlarms()
            return list
        }

        override fun onPostExecute(result: List<Alarm>?) {
            super.onPostExecute(result)
            if (result != null){
                presenter.onAlarmListFetched(result)
            }
        }

    }
}