package org.leftbrained.uptaskapp.classes

import android.content.SharedPreferences
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class Stats(
    var userId: Int = 0,
    var totalTasks: Int = 0,
    var completedTasks: Int = 0,
    var undoneTasks: Int = 0
) {
    fun checkStats(
        sharedPref: SharedPreferences,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        userId: Int
    ): Stats {
        val logs = sharedPref.getStringSet("logs", mutableSetOf())
        val totalTasks = logs?.count {
            val date = LocalDate.parse(it.split(" - ")[1])
            println("Date: $date")
            val action = it.split(" - ")[3]
            println("Action: $action")
            println("User ID: $userId")
            date in startDate.date..endDate.date && action == "Add" && userId == it.split(" - ")[2].toInt()
        } ?: 0
        println("Total tasks when counted: $totalTasks")
        val completedTasks = logs?.count {
            val date = LocalDate.parse(it.split(" - ")[1])
            val action = it.split(" - ")[3]
            date in startDate.date..endDate.date && action == "Done" && userId == it.split(" - ")[2].toInt()
        } ?: 0
        val undoneTasks = logs?.count {
            val date = LocalDate.parse(it.split(" - ")[1])
            val action = it.split(" - ")[3]
            date in startDate.date..endDate.date && action == "Undone" && userId == it.split(" - ")[2].toInt()
        } ?: 0
        this.completedTasks = completedTasks
        this.totalTasks = totalTasks
        this.undoneTasks = undoneTasks
        return Stats(userId, totalTasks, completedTasks, undoneTasks)
    }
}