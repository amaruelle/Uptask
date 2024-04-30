package org.leftbrained.uptaskapp.dialogs

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.classes.Checks.dateCheck
import org.leftbrained.uptaskapp.classes.Checks.emptyCheck
import org.leftbrained.uptaskapp.classes.Checks.priorityCheck
import org.leftbrained.uptaskapp.classes.Checks.tagCheck
import org.leftbrained.uptaskapp.classes.Logs
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UserTask
import org.leftbrained.uptaskapp.viewmodel.TagViewModel
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel

@Composable
fun AddTaskDialog(onDismissRequest: () -> Unit, taskList: TaskList, vm: DatabaseStateViewmodel = viewModel()) {
    val taskVm by remember { mutableStateOf(TaskViewModel()) }
    val tagVm by remember { mutableStateOf(TagViewModel()) }
    var name by remember { mutableStateOf("My Task") }
    var desc by remember { mutableStateOf("My Description") }
    val logs = Logs(LocalContext.current.getSharedPreferences("logs", Context.MODE_PRIVATE))
    var dueDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        )
    }
    val tags = remember {
        mutableListOf<Tag>()
    }
    val context = LocalContext.current
    var priority by remember { mutableIntStateOf(0) }
    var tagEnter by remember { mutableStateOf("") }
    val userId = transaction { taskList.userId }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.add_task),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.enter_values_for_new_task),
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
                LazyRow {
                    items(tags) { tag ->
                        AssistChip(
                            label = { Text(text = tag.tag) },
                            onClick = {
                                tagVm.removeTag(tag.id.value)
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
                                val newTag = tagVm.newTag(tagEnter, UserTask.all().last())
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
                Row{
                    AssistChip(onClick = {
                    }, label = { Text("Pick attachment") })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        if (!dateCheck(dueDate, context)) return@Button
                        if (!emptyCheck(name, desc, context)) return@Button
                        if (!priorityCheck(priority, context)) return@Button
                        transaction {
                            taskVm.newTask(name, desc, dueDate, priority, taskList, userId)
                            val newTaskId = UserTask.all().last()
                            for (tag in tags) {
                                val newTag = Tag[tag.id.value]
                                newTag.taskId = newTaskId
                            }
                            vm.databaseState++
                            logs.addTaskLog(userId, newTaskId)
                        }
                        onDismissRequest()
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.add))
                    }
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}
