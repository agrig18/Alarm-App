package ge.agrigalashvili.alarmapp

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import ge.agrigalashvili.alarmapp.data.entity.Alarm
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, IMainView, AlarmListListener {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var modeTxt: TextView
    private lateinit var addButton: ImageButton

    private lateinit var rvAlarms: RecyclerView
    private lateinit var presenter: MainPresenter
    private var adapter = AlarmAdapter(this)

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = this.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        presenter = MainPresenter(this)

        presenter.getAlarms()
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
        Toast.makeText(this,hourOfDay.toString(),Toast.LENGTH_SHORT).show()
        var alarm = Alarm("$hourOfDay:$minute")
        presenter.addAlarm(alarm)
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
        presenter.deleteAlarm(alarm)
        adapter.notifyDataSetChanged()
    }

    companion object{
        const val NIGHT_MODE = "NIGHT_MODE"
        const val PREFERENCE_FILE = "FILENAME"
    }

    override fun showLastAlarmTime(alarms: List<Alarm>) {
        adapter.list = alarms
        adapter.notifyDataSetChanged()
    }

}

interface AlarmListListener{
    fun onAlarmItemClicked(alarm: Alarm)
}