package ge.agrigalashvili.alarmapp

import ge.agrigalashvili.alarmapp.data.entity.Alarm

interface IMainPresenter {
    fun onAlarmListFetched(alarms: List<Alarm>)
}