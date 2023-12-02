package org.leftbrained.uptaskapp.classes

import org.jetbrains.exposed.sql.transactions.transaction

class TaskListsViewmodel {
    fun getTaskLists(userId: Int): List<TaskList> {
        transaction {
            return@transaction TaskList.find { UptaskDb.TaskLists.userId eq userId }.toList<TaskList>()
        }
        return emptyList()
    }

    fun getTaskList(taskListId: Int): TaskList? {
        transaction {
            return@transaction TaskList.find { UptaskDb.TaskLists.id eq taskListId }.firstOrNull()
        }
        return null
    }

    fun addTaskList(taskList: TaskList) {
        transaction {
            TaskList.new {
                userId = taskList.userId
                name = taskList.name
                emoji = taskList.emoji
            }
        }
    }

    fun deleteTaskList(taskListId: Int) {
        transaction {
            TaskList.find { UptaskDb.TaskLists.id eq taskListId }.firstOrNull()?.delete()
        }
    }
}