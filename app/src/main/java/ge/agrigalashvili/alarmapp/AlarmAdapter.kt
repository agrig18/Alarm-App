package ge.agrigalashvili.alarmapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ge.agrigalashvili.alarmapp.data.entity.Alarm


class AlarmAdapter(val listListener: AlarmListListener): RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    var list = listOf<Alarm>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.alarm_info_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        var alarm = list[position]
        holder.bindAlarm(alarm)

        holder.itemView.setOnLongClickListener{
            listListener.onAlarmItemClicked(alarm)
            return@setOnLongClickListener true
        }
        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                listListener.onAlarmSet(alarm)
            }else{
                listListener.onAlarmUnset(alarm)
            }
        }
    }


    inner class AlarmViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bindAlarm(alarm: Alarm) {
            timeTxt.text = alarm.timeSet
            switch.isChecked = true
        }

        val timeTxt = view.findViewById<TextView>(R.id.timeTxt)
        val switch = view.findViewById<Switch>(R.id.toggleSwitch)
    }

}