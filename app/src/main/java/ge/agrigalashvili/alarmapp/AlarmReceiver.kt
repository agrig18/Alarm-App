package ge.agrigalashvili.alarmapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ge.agrigalashvili.alarmapp.MainActivity.Companion.ALARM_TIME

class AlarmReceiver: BroadcastReceiver() {
    private lateinit var time: String
    private var notificationId: Int = DEFAULT_VALUE

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if (intent != null) {
                notificationId = intent.getIntExtra(NOTIFICATION_ID, DEFAULT_VALUE)
            }
            if (intent?.action == ALARM_ACTION_NAME) {
                notificationId = System.currentTimeMillis().toInt()
                time = intent.getStringExtra(ALARM_TIME).toString()

                var notificationManager = NotificationManagerCompat.from(context)

                val notificationClickPendingIntent = PendingIntent.getActivity(
                    context,
                    notificationId,
                    Intent(context, MainActivity::class.java),
                    0
                )

                val cancelClickIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    Intent(NOTIFICATION_CLICK).apply {
                        `package` = context.packageName
                        putExtra(NOTIFICATION_ID, notificationId)
                    },
                    0
                )

                val snoozeButtonClick = PendingIntent.getBroadcast(
                    context,
                    notificationId*10,
                    Intent(NOTIFICATION_CLICK).apply {
                        `package` = context.packageName
                        putExtra(NOTIFICATION_ID, notificationId)
                        putExtra(ALARM_TIME, time)
                        putExtra(SNOOZE_MODE, 1)
                    },
                    0
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createChannel(notificationManager)
                }

                var notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.alarm_clock)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_TEXT + time)
                    .setContentIntent(notificationClickPendingIntent)
                    .addAction(R.mipmap.alarm_clock,
                        CANCEL_LABEL, cancelClickIntent)
                    .addAction(R.mipmap.alarm_clock,
                        SNOOZE_LABEL, snoozeButtonClick)
                notificationManager.notify(notificationId, notification.build())
            }else{
                var notificationManager = NotificationManagerCompat.from(context)

                if (notificationId != 0) {
                    notificationManager.cancel(notificationId)
                }

                if (intent?.getIntExtra(SNOOZE_MODE, DEFAULT_VALUE) == 1){
                    MainActivity.start(context, intent.getStringExtra(ALARM_TIME))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager: NotificationManagerCompat) {
        var notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val TAG = "ALARM_RECEIVER"
        const val ALARM_ACTION_NAME = "ge.agrigalashvili.alarmapp.ALARM_ACTION"
        const val NOTIFICATION_CLICK = "ge.agrigalashvili.alarmapp.NOTIFICATION_CLICK"
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val CHANNEL_ID = "ge.agrigalashvili.alarmapp.CHANNEL_1"
        const val CHANNEL_NAME = "CHANNEL1"
        const val NOTIFICATION_TITLE = "Alarm message!"
        const val NOTIFICATION_TEXT = "Alarm set on "
        const val CANCEL_LABEL = "Cancel"
        const val SNOOZE_LABEL = "Snooze"
        const val SNOOZE_MODE = "SNOOZE_MODE"
        const val DEFAULT_VALUE = 0
    }
}