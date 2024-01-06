package org.leftbrained.uptaskapp.classes

import androidx.lifecycle.ViewModel
import org.jetbrains.exposed.sql.transactions.transaction

class TaskListsViewmodel : ViewModel() {
    fun getTaskLists(userId: Int): List<TaskList> = transaction {
        TaskList.find { UptaskDb.TaskLists.userId eq userId }.toList()
    }

    fun getTaskList(taskListId: Int): TaskList? = transaction {
        TaskList.find { UptaskDb.TaskLists.id eq taskListId }.firstOrNull()
    }

    fun addTaskList(taskList: TaskList) = transaction {
        TaskList.new {
            userId = taskList.userId
            name = taskList.name
            emoji = taskList.emoji
        }
    }

    fun deleteTaskList(taskListId: Int) = transaction {
        TaskList.find { UptaskDb.TaskLists.id eq taskListId }.firstOrNull()?.delete()
    }
}