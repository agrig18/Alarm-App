package ge.agrigalashvili.alarmapp

import ge.agrigalashvili.alarmapp.data.entity.Alarm

interface IMainView {
    fun showLastAlarmTime(alarms: List<Alarm>)
}