package ge.agrigalashvili.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import ge.agrigalashvili.alarmapp.AlarmReceiver.Companion.SNOOZE_MODE
import ge.agrigalashvili.alarmapp.AlarmReceiver.Companion.TAG
import ge.agrigalashvili.alarmapp.data.entity.Alarm
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, IMainView, AlarmListListener {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var modeTxt: TextView
    private lateinit var addButton: ImageButton

    private lateinit var rvAlarms: RecyclerView
    private lateinit var presenter: MainPresenter
    private var adapter = AlarmAdapter(this)

    private var isDarkMode = false

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = this.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        presenter = MainPresenter(this)

        presenter.getAlarms()

        if (intent.getIntExtra(SNOOZE_MODE, 0) == 1){
            val newTime = get1MinuteIncreasedTime(1)
            startAlarm(Alarm(newTime))
        }
    }

    private fun get1MinuteIncreasedTime(minute: Int): String {
        val curTime = intent.getStringExtra(ALARM_TIME)
        val originalFormat = SimpleDateFormat("HH:mm")
        val date = originalFormat.parse(curTime)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, minute)
        return originalFormat.format(calendar.time)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun initView() {
        rvAlarms = findViewById(R.id.rvAlarms)
        rvAlarms.adapter = adapter

        modeTxt = findViewById(R.id.lightDarkModeTxt)
        addButton = findViewById(R.id.addButton)

        if (loadNightModeState()){
            setNightModeState(true, AppCompatDelegate.MODE_NIGHT_YES, R.string.light_txt)
        }

        modeTxt.setOnClickListener{
            if (isDarkMode){
                setNightModeState(false, AppCompatDelegate.MODE_NIGHT_NO, R.string.dark_txt)
            }else{
                setNightModeState(true, AppCompatDelegate.MODE_NIGHT_YES, R.string.light_txt)
            }
            isDarkMode = !isDarkMode
        }

        addButton.setOnClickListener{
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        var calendar = Calendar.getInstance()

        var timePickerDialog = TimePickerDialog(
            this,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val formattedDate = formatDate(hourOfDay, minute)
        var alarm = Alarm(formattedDate)
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = hourOfDay
        cal[Calendar.MINUTE] = minute
        if (cal.timeInMillis < System.currentTimeMillis()){
            Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_SHORT).show()
            return
        }
        presenter.addAlarm(alarm)
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun startAlarm(alarm: Alarm) {
        var pi: PendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            Intent(AlarmReceiver.ALARM_ACTION_NAME).apply {
                `package` = packageName
                putExtra(ALARM_TIME, alarm.timeSet)
            },
            PendingIntent.FLAG_UPDATE_CURRENT)
        var alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val cal = Calendar.getInstance()
        var indx = alarm.timeSet.indexOf(":")
        var hourOfDay = (alarm.timeSet.substring(0, indx)).toInt()
        var minute = (alarm.timeSet.substring(indx+1, alarm.timeSet.length)).toInt()
        cal[Calendar.HOUR_OF_DAY] = hourOfDay
        cal[Calendar.MINUTE] = minute

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }

    private fun stopAlarm() {
        var pi = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            Intent(AlarmReceiver.ALARM_ACTION_NAME).apply {
                `package` = packageName
            },
            0
        )
        var alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pi)
    }

    private fun formatDate(hourOfDay: Int, minute: Int): String {
        val originalFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val date = originalFormat.parse("$hourOfDay:$minute")
        return originalFormat.format(date)
    }

    private fun setNightModeState(state: Boolean, mode: Int, id: Int){
        var editor = sharedPref.edit()
        editor.putBoolean(NIGHT_MODE, state)
        editor.commit()

        AppCompatDelegate.setDefaultNightMode(mode)
        modeTxt.text = resources.getString(id);
    }

    private fun loadNightModeState(): Boolean {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            isDarkMode = true
        }
        return sharedPref.getBoolean(NIGHT_MODE, isDarkMode)
    }

    override fun onAlarmItemClicked(alarm: Alarm) {
        deleteAlarmDialog(alarm)
    }

    private fun deleteAlarmDialog(alarm: Alarm) {
        var dialog = AlertDialog.Builder(this)
            .setMessage(R.string.alert_message)
            .setCancelable(false)
            .setPositiveButton(R.string.label_yes
            ) { _, _ ->
                stopAlarm()
                presenter.deleteAlarm(alarm)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton(
                R.string.label_no
            ) { _, _ -> }
            .create()

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onAlarmSet(alarm: Alarm) {
        startAlarm(alarm)
    }

    override fun onAlarmUnset(alarm: Alarm) {
        stopAlarm()
    }

    companion object{
        const val NIGHT_MODE = "NIGHT_MODE"
        const val PREFERENCE_FILE = "FILENAME"
        const val TOAST_TEXT = "Can't set alarm earlier than now"
        const val ALARM_TIME = "ALARM_TIME"

        fun start(context: Context, time: String?){
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                putExtra(SNOOZE_MODE, 1)
                putExtra(ALARM_TIME, time)
            })
        }
    }

    override fun showLastAlarmTime(alarms: List<Alarm>) {
        adapter.list = alarms
        adapter.notifyDataSetChanged()
    }

}

interface AlarmListListener{
    fun onAlarmItemClicked(alarm: Alarm)
    fun onAlarmSet(alarm: Alarm)
    fun onAlarmUnset(alarm: Alarm)
}