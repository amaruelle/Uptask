package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.UptaskDb

@Composable
fun ModifyTaskListDialog(onDismissRequest: () -> Unit, taskListId: Int, vm: DatabaseStateViewmodel = viewModel()) {
    val taskList by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                TaskList.find { UptaskDb.TaskLists.id eq taskListId }.elementAt(0)
            }
        }
    }
    var name by remember { mutableStateOf(taskList.name) }
    var emoji by remember { mutableStateOf(taskList.emoji) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Modify Task List",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Please enter values for modified task list",
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
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        transaction {
                            taskList.name = name
                            taskList.emoji = emoji
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
                                taskList.delete()
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