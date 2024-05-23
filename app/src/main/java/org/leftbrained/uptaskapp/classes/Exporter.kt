package org.leftbrained.uptaskapp.classes

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.UserTask

class Exporter {
    fun dataToMarkdown(userLogin: String): String {
        val user = transaction {
            User.find {
                UptaskDb.Users.login eq userLogin
            }
        }.toList().first()
        val userTasks = transaction {
            UserTask.find {
                UptaskDb.UserTasks.userId eq user.id
            }
        }.toList()

        val exported: StringBuilder = StringBuilder()
        exported.append("TaskList, Task, Description, Tags, Priority, Due Date, Status\n")
        userTasks.forEach {
            val tags = transaction {
                Tag.find {
                    UptaskDb.TaskTags.taskId eq it.id
                }
            }.toList()
            exported.append(
                "${it.taskListId.emoji} ${it.taskListId.name}, ${it.task}, ${it.description}, (${
                    tags.joinToString(
                        "; "
                    ) { tag -> tag.tag }
                }), ${it.priority}, ${it.dueDate}, ${it.isDone}\n"
            )
        }

        return exported.toString()
    }
}