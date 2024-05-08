package org.leftbrained.uptaskapp.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.Logs
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask
import org.leftbrained.uptaskapp.dialogs.ModifyTaskDialog
import java.util.Locale

@Composable
fun TaskView(taskId: Int, vm: DatabaseStateViewmodel = viewModel()) {

    val task = remember(vm.databaseState) {
        transaction {
            UserTask.find(UptaskDb.UserTasks.id eq taskId).elementAt(0)
        }
    }
    val taskTags =
        remember(vm.databaseState) {
            transaction {
                Tag.find { UptaskDb.TaskTags.taskId eq taskId }.toList()
            }
        }
    val sharedPref = LocalContext.current.getSharedPreferences("logs", Context.MODE_PRIVATE)
    val userId = transaction {
        task.userId.id.value
    }
    var showEdit by remember(vm.databaseState) { mutableStateOf(false) }
    val logs = Logs(sharedPref)
    Row(
        Modifier
            .padding(12.dp)
            .background(
                if (task.dueDate != null) {
                    if (task.dueDate!! < Clock.System
                            .now()
                            .toLocalDateTime(TimeZone.UTC)
                    ) {
                        MaterialTheme.colorScheme.errorContainer
                    } else MaterialTheme.colorScheme.surfaceVariant
                } else MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.isDone, onCheckedChange = {
            transaction {
                task.isDone = !task.isDone
                logs.statusChangeLog(userId, task)
            }
            vm.databaseState++
        })
        Box(
            Modifier
                .size(24.dp)
                .background(
                    when (task.priority) {
                        0 -> MaterialTheme.colorScheme.error
                        1 -> MaterialTheme.colorScheme.tertiary
                        2 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    }, shape = RoundedCornerShape(12.dp)
                ), contentAlignment = Alignment.Center
        ) {
            Text(
                text = task.priority.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = when (task.priority) {
                    0 -> MaterialTheme.colorScheme.onError
                    1 -> MaterialTheme.colorScheme.onTertiary
                    2 -> MaterialTheme.colorScheme.onSecondary
                    else -> MaterialTheme.colorScheme.surface
                }
            )
        }
        Column(Modifier.padding(12.dp)) {
            Text(
                text = task.task,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold
            )
            if (task.description != "") {
                Text(
                    text = task.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (task.dueDate != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = "Date range icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = task.dueDate?.let {
                            "${it.dayOfMonth} ${
                                it.month.name.let { name ->
                                    name.substring(0, 1)
                                        .uppercase(Locale.ROOT) + name.substring(1)
                                        .lowercase(Locale.ROOT)

                                }
                            }, ${it.year} ${if (it.hour != 0 && it.minute != 0) "${it.hour}:${it.minute}" else ""}"
                        }
                            .toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
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
            if (task.attachment != null) {
                Row {
                    AssistChip(
                        label = { Text(task.attachment!!) },
                        onClick = {},
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {
            showEdit = true
        }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Task settings icon",
            )
        }
        if (showEdit) {
            ModifyTaskDialog(onDismissRequest = { showEdit = false }, taskId = taskId)
        }
    }
}