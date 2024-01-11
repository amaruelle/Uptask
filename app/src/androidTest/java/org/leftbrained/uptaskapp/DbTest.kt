package org.leftbrained.uptaskapp

import kotlinx.datetime.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.leftbrained.uptaskapp.db.*

class DbTest {
    @Test
    fun testTableCreation() {
        connectToDb()
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UptaskDb.TaskLists,
                UptaskDb.Users,
                UptaskDb.UserTasks,
                UptaskDb.TaskTags
            )
            assertNotNull(UptaskDb.Users)
            assertNotNull(UptaskDb.UserTasks)
            assertNotNull(UptaskDb.TaskTags)
            assertNotNull(UptaskDb.TaskLists)
            if (User.findById(1) == null) {
                User.new {
                    login = "testLogin"
                    password = "testPassword"
                }
            }
        }
        transaction {
            if (TaskList.findById(1) == null) {
                TaskList.new {
                    userId = User.findById(1)!!
                    name = "testList"
                    emoji = "testEmoji"
                }
            }
        }
        transaction {
            val currentMoment: Instant = Clock.System.now()
            val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
            val localDate = datetimeInSystemZone.date
            if (UserTask.findById(1) == null) {
                UserTask.new {
                    userId = User.findById(1)!!
                    taskListId = TaskList.findById(1)!!
                    task = "testTask"
                    description = "testDescription"
                    dueDate = localDate
                    isDone = false
                    priority = 3
                }
            }

            transaction {
                User.all().forEach { println(it.login) }
                TaskList.all().forEach { println(it.name) }
                UserTask.all().forEach { println(it.task) }
            }
        }
    }
}