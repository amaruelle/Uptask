package org.leftbrained.uptaskapp.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.leftbrained.uptaskapp.dates.DateUtils
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel
import kotlin.time.Duration

object ReminderUtils {
    fun setReminder(
        context: Context,
        name: String,
        desc: String,
        dueDate: Instant?,
        selectedReminder: Pair<String, Duration>
    ): Boolean {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("taskName", name)
        intent.putExtra("taskDesc", desc)
        intent.putExtra(
            "taskDueDate",
            DateUtils.instantToDate(dueDate)
        )
        println(DateUtils.instantToDate(dueDate))
        val pendingIntent = PendingIntent.getBroadcast(
            context, Clock.System.now().epochSeconds.toInt(), intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val reminderMillis = dueDate!! - selectedReminder.second
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderMillis.toEpochMilliseconds(),
                pendingIntent
            )
            return true
        } else return false
    }
}