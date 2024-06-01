package org.leftbrained.uptaskapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.UserTask

class TaskViewModel : ViewModel() {
    fun getTaskById(taskId: Int): UserTask = transaction {
        return@transaction UserTask.find(UptaskDb.UserTasks.id eq taskId).elementAt(0)
    }

    fun newTask(
        name: String,
        desc: String? = "",
        dueDate: LocalDateTime?,
        priority: Int?,
        taskList: TaskList,
        userId: User
    ): UserTask = transaction {
        UserTask.new {
            this.task = name
            this.description = desc
            this.dueDate = dueDate
            this.priority = priority.toString().toInt()
            this.taskListId = taskList
            this.isDone = false
            this.userId = userId
            this.attachment = attachment
        }
    }

    fun removeTask(taskId: Int) = transaction {
        UserTask.find(UptaskDb.UserTasks.id eq taskId).elementAt(0).delete()
    }

    fun getNextTask() = transaction { (UserTask.all().count() + 1).toInt() }
}