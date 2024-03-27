package org.leftbrained.uptaskapp.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Log
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UptaskDb.Logs.date
import org.leftbrained.uptaskapp.db.UserTask

@Composable
fun TaskView(navController: NavController, taskId: Int, vm: DatabaseStateViewmodel = viewModel()) {
    val task =
        remember { transaction { UserTask.find(UptaskDb.UserTasks.id eq taskId).elementAt(0) } }
    val taskTags =
        remember { transaction { Tag.find { UptaskDb.TaskTags.taskId eq taskId }.toList() } }
    val sharedPref = (LocalContext.current as Activity).getPreferences(Context.MODE_PRIVATE)
    val userId = transaction {
        task.userId.id.value
    }
    Row(
        Modifier
            .padding(12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.isDone, onCheckedChange = {
            transaction {
                task.isDone = !task.isDone
//                Log.new {
//                    userId = task.userId
//                    date = LocalDate.parse(
//                        Clock.System.now()
//                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
//                    )
//                    action = if (task.isDone) "Done" else "Undone"
//                    this.taskId = task
//                }
                with(sharedPref.edit()) {
                    val logs = sharedPref.getStringSet("logs", mutableSetOf())
                    val newLogs = logs?.toMutableSet()
                    newLogs?.add(
                        "${logs.size + 1} - ${
                            LocalDate.parse(
                                Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                            )
                        } - $userId - ${if (task.isDone) "Done" else "Undone"} - ${task.id.value}"
                    )
                    putStringSet("logs", newLogs)
                    apply()
                }
            }
            vm.databaseState++
        })
        Column(Modifier.padding(12.dp)) {
            Text(
                text = task.task,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.DateRange,
                    contentDescription = "Date range icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = task.dueDate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row {
                for (tag in taskTags) {
                    AssistChip(
                        label = { Text(text = tag.tag) },
                        onClick = { tag.delete() },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Row {
                AssistChip(
                    label = { Text(text = task.attachment!!.split("/").last()) },
                    onClick = {},
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {
            navController.navigate("modifyTask/${taskId}")
        }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Task settings icon",
            )
        }
    }
}