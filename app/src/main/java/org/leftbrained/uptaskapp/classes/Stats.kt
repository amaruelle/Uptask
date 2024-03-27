package org.leftbrained.uptaskapp.classes

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.and
import org.leftbrained.uptaskapp.db.Log
import org.leftbrained.uptaskapp.db.UptaskDb

data class Stats(
    var userId: Int,
    var totalTasks: Int,
    var completedTasks: Int,
    var undoneTasks: Int
)