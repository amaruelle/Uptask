package org.leftbrained.uptaskapp.classes

import androidx.lifecycle.ViewModel
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UptaskDb

class TaskListsViewmodel {
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