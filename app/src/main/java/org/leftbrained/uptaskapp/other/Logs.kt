package org.leftbrained.uptaskapp.other

import android.content.SharedPreferences
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.UserTask

class Logs(var sharedPref: SharedPreferences) {
    private var logsSet = sharedPref.getStringSet("logs", mutableSetOf())!!

    fun statusChangeLog(
        userId: Int,
        task: UserTask
    ) {
        with(sharedPref.edit()) {
            val newLogs = logsSet.toMutableSet()
            newLogs.add(
                "${logsSet.size + 1} - ${
                    LocalDate.parse(
                        Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                    )
                } - $userId - ${if (task.isDone) "Done" else "Undone"} - ${task.id.value}"
            )
            putStringSet("logs", newLogs)
            apply()
        }
    }

    fun addTaskLog(
        userId: User,
        newTaskId: UserTask
    ) {
        with(sharedPref.edit()) {
            val newLogs = logsSet.toMutableSet()
            newLogs.add(
                "${logsSet.size + 1} - ${
                    LocalDate.parse(
                        Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                    )
                } - ${userId.id.value} - Add - ${newTaskId.id.value}"
            )
            putStringSet("logs", newLogs)
            apply()
        }
    }

    fun deleteTaskLog(
        userId: Int,
        task: UserTask
    ) {
        with(sharedPref.edit()) {
            val newLogs = logsSet.toMutableSet()
            newLogs.add(
                "${logsSet.size + 1} - ${
                    LocalDate.parse(
                        Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                    )
                } - $userId - Delete - ${task.id.value}"
            )
            putStringSet("logs", newLogs)
            apply()
        }
    }

}