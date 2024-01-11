package org.leftbrained.uptaskapp.viewmodel

import androidx.lifecycle.ViewModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask

class TaskViewModel : ViewModel() {
    fun getTaskById(taskId: Int): UserTask = transaction {
        return@transaction UserTask.find(UptaskDb.UserTasks.id eq taskId).elementAt(0)
    }

}