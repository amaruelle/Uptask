package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.classes.Tag
import org.leftbrained.uptaskapp.classes.UptaskDb
import org.leftbrained.uptaskapp.classes.UserTask

@Composable
fun ModifyTaskDialog(onDismissRequest: () -> Unit, taskId: Int, vm: DatabaseStateViewmodel = viewModel()) {
    val task by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                UserTask.find { UptaskDb.UserTasks.id eq taskId }.elementAt(0)
            }
        }
    }
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
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Modify Task",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Please enter values for modified task",
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
                    OutlinedTextField(
                        value = tagEnter,
                        onValueChange = { tagEnter = it },
                        modifier = Modifier
                            .width(100.dp)
                            .padding(top = 16.dp),
                        label = { Text("Tags") })
                    IconButton(onClick = {
                        transaction {
                            Tag.new {
                                tag = tagEnter
                                this.taskId = task
                            }
                        }
                        tagEnter = ""
                        vm.databaseState++
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add tag icon"
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        transaction {
                            task.task = name
                            task.description = desc
                            task.dueDate = LocalDate.parse(dueDate)
                            task.priority = priority
                        }
                        vm.databaseState++
                        onDismissRequest()
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = "Modify")
                    }
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Cancel")
                    }
                    IconButton(
                        onClick = {
                            transaction {
                                task.delete()
                            }
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