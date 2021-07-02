package ge.agrigalashvili.alarmapp

import ge.agrigalashvili.alarmapp.data.entity.Alarm

class MainPresenter(var view: IMainView?): IMainPresenter {

    private val interactor = MainInteractor(this)

    fun getAlarms(){
        interactor.getAlarmsListFromDatabase()
    }

    fun addAlarm(alarm: Alarm){
        interactor.addAlarmToDatabase(alarm)
    }

    fun deleteAlarm(alarm: Alarm){
        interactor.deleteAlarmFromDatabase(alarm)
    }

    override fun onAlarmListFetched(alarms: List<Alarm>){
        view?.showLastAlarmTime(alarms)
    }

    fun detachView() {
        view = null
    }
}