package org.leftbrained.uptaskapp.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

class UptaskDb {
    object Users: IntIdTable() {
        val login = varchar("login", 50).uniqueIndex()
        val password = varchar("password", 50)
    }

    object UserTasks: IntIdTable() {
        val userId = reference("userId", Users, onDelete = ReferenceOption.CASCADE)
        val taskListId = reference("taskListId", TaskLists, onDelete = ReferenceOption.CASCADE)
        val task = varchar("task", 50)
        val description = varchar("description", 450).nullable()
        val dueDate = datetime("dueDate").nullable()
        val isDone = bool("isDone")
        val priority = integer("priority").nullable()
        val attachment = varchar("attachment", 150).nullable()
    }

    object TaskTags: IntIdTable() {
        val taskId = reference("taskId", UserTasks, onDelete = ReferenceOption.CASCADE).nullable()
        val tag = varchar("tag", 50)
    }

    object TaskLists: IntIdTable() {
        val userId = reference("userId", Users, onDelete = ReferenceOption.CASCADE)
        val name = varchar("name", 50)
        val emoji = varchar("emoji", 50)
    }

    object Logs: IntIdTable() {
        val userId = reference("userId", Users)
        val date = date("date")
        val action = varchar("action", 50)
        val taskId = reference("taskId", UserTasks, onDelete = ReferenceOption.CASCADE).nullable()
    }
}
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(UptaskDb.Users)

    var login by UptaskDb.Users.login
    var password by UptaskDb.Users.password
}

class UserTask(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTask>(UptaskDb.UserTasks)

    var userId by User referencedOn UptaskDb.UserTasks.userId
    var taskListId by TaskList referencedOn UptaskDb.UserTasks.taskListId
    var task by UptaskDb.UserTasks.task
    var description by UptaskDb.UserTasks.description
    var dueDate by UptaskDb.UserTasks.dueDate
    var isDone by UptaskDb.UserTasks.isDone
    var priority by UptaskDb.UserTasks.priority
    var attachment by UptaskDb.UserTasks.attachment
}

class Tag(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Tag>(UptaskDb.TaskTags)

    var taskId by UserTask optionalReferencedOn UptaskDb.TaskTags.taskId
    var tag by UptaskDb.TaskTags.tag
}

class TaskList(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskList>(UptaskDb.TaskLists)

    var userId by User referencedOn UptaskDb.TaskLists.userId
    var name by UptaskDb.TaskLists.name
    var emoji by UptaskDb.TaskLists.emoji
}

class Log(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Log>(UptaskDb.Logs)

    var userId by User referencedOn UptaskDb.Logs.userId
    var taskId by UserTask optionalReferencedOn UptaskDb.Logs.taskId
    var date by UptaskDb.Logs.date
    var action by UptaskDb.Logs.action
}

fun connectToDb() {
    Database.connect("jdbc:h2:file:/data/data/org.leftbrained.uptaskapp/databases/uptask.db", driver = "org.h2.Driver")
}