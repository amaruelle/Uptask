package org.leftbrained.uptaskapp.dialogs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.classes.Checks
import org.leftbrained.uptaskapp.classes.Checks.dateCheck
import org.leftbrained.uptaskapp.classes.Checks.tagCheck
import org.leftbrained.uptaskapp.classes.Logs
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Log
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask
import org.leftbrained.uptaskapp.viewmodel.TagViewModel
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel

@Composable
fun ModifyTaskDialog(
    onDismissRequest: () -> Unit,
    taskId: Int,
    vm: DatabaseStateViewmodel = viewModel()
) {
    val scrollState = rememberScrollState()
    val logs = Logs(LocalContext.current.getSharedPreferences("logs", Context.MODE_PRIVATE))
    val taskVm = remember { TaskViewModel() }
    val task by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                UserTask.find { UptaskDb.UserTasks.id eq taskId }.elementAt(0)
            }
        }
    }
    var showExpandableTags by remember { mutableStateOf(false) }
    val tags = remember {
        mutableListOf<Tag>()
    }
    val context = LocalContext.current
    var name by remember { mutableStateOf(task.task) }
    var desc by remember { mutableStateOf(task.description) }
    var dueDate = remember {
        task.dueDate.toString()
    }
    var priority by remember { mutableIntStateOf(task.priority) }
    var tagEnter by remember { mutableStateOf("") }
    val taskTags by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                Tag.find { UptaskDb.TaskTags.taskId eq taskId }.toList()
            }
        }
    }
    val userId by remember {
        transaction { mutableIntStateOf(task.userId.id.value) }
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp).verticalScroll(scrollState)) {
                Text(
                    text = stringResource(R.string.modify_task),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.please_enter_values_task),
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = priority.toString(),
                    onValueChange = { priority = if (it == "") 0 else it.toInt() },
                    label = { Text(stringResource(R.string.priority)) },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text(stringResource(R.string.due_date)) },
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (tag in taskTags) {
                        AssistChip(
                            label = { Text(text = tag.tag) },
                            onClick = {
                                transaction {
                                    tag.delete()
                                }
                                vm.databaseState++
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                OutlinedTextField(
                    maxLines = 1,
                    value = tagEnter,
                    onValueChange = { tagEnter = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    label = { Text(stringResource(R.string.tag)) }, trailingIcon = {
                        IconButton(onClick = {
                            if (!tagCheck(tagEnter, context)) return@IconButton
                            transaction {
                                val newTag = Tag.new {
                                    tag = tagEnter
                                    this.taskId = UserTask[taskId]
                                }
                                tags.add(newTag)
                            }
                            tagEnter = ""
                            vm.databaseState++
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add tag icon"
                            )
                        }
                    })
                Row(Modifier.padding(top = 12.dp)) {
                    AssistChip(onClick = {}, label = { Text("Pick attachment") }, leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = "Add attachment icon"
                        )
                    })
                }
                Row(
                    Modifier
                        .clickable {
                            showExpandableTags = !showExpandableTags
                        }
                        .padding(top = 12.dp, bottom = 6.dp)) {
                    Text("Click to select existing")
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = "Add tag icon"
                    )
                }
                if (showExpandableTags) {
                    LazyRow {
                        val distinctTags = transaction { (Tag.all().groupBy { it.tag }) }
                        transaction {
                            for (el in distinctTags) {
                                item {
                                    AssistChip(onClick = {
                                        tags.add(el.value.first())
                                        vm.databaseState++
                                    }, label = { Text(el.key) },
                                        Modifier.padding(end = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        if (!dateCheck(dueDate, context)) return@Button
                        if (!Checks.emptyCheck(name, desc, context)) return@Button
                        if (!Checks.priorityCheck(priority, context)) return@Button
                        transaction {
                            task.task = name
                            task.description = desc
                            task.dueDate = LocalDate.parse(dueDate)
                            task.priority = priority
                        }
                        onDismissRequest()
                        vm.databaseState++
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.modify))
                    }
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    IconButton(
                        onClick = {
                            taskVm.removeTask(taskId)
                            logs.deleteTaskLog(userId, task)
                            onDismissRequest()
                            vm.databaseState++
                        },
                        modifier = Modifier.width(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    }
}