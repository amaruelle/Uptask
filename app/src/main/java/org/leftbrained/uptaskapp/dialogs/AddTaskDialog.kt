package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UserTask

@Composable
fun AddTaskDialog(onDismissRequest: () -> Unit, taskList: TaskList, vm: DatabaseStateViewmodel = viewModel()) {
    var name by remember { mutableStateOf("My Task") }
    var desc by remember { mutableStateOf("My Description") }
    var dueDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        )
    }
    val tags = remember {
        mutableListOf<Tag>()
    }
    var priority by remember { mutableIntStateOf(0) }
    var tagEnter by remember { mutableStateOf("") }
    val userId = transaction { taskList.userId }
    val taskTags by remember(vm.databaseState) {
        derivedStateOf {
            listOf<Tag>()
        }
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Add Task",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enter values for new task",
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = priority.toString(),
                    onValueChange = { priority = if (it == "") 0 else it.toInt() },
                    label = { Text("Priority") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                LazyRow {
                    items(tags) { tag ->
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
                    label = { Text("Tag") }, trailingIcon = {
                        IconButton(onClick = {
                            transaction {
                                val newTag = Tag.new {
                                    tag = tagEnter
                                    this.taskId = null
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        transaction {
                            UserTask.new {
                                this.task = name
                                this.description = desc
                                this.dueDate = LocalDate.parse(dueDate)
                                this.priority = priority.toString().toInt()
                                this.taskListId = taskList
                                this.isDone = false
                                this.userId = userId
                            }
                            val newTaskId = UserTask.all().last()
                            for (tag in tags) {
                                val newTag = Tag[tag.id.value]
                                newTag.taskId = newTaskId
                            }
                            vm.databaseState++
                        }
                        onDismissRequest()
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = "Add")
                    }
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}