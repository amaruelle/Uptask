package org.leftbrained.uptaskapp.viewmodel

import androidx.lifecycle.ViewModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.UptaskDb
import org.leftbrained.uptaskapp.classes.UserTask

class TaskViewModel : ViewModel() {
    fun getTaskById(taskId: Int): UserTask = transaction {
        return@transaction UserTask.find(UptaskDb.UserTasks.id eq taskId).elementAt(0)
    }

}