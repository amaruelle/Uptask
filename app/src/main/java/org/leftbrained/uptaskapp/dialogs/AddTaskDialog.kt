package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.UserTask

@Composable
fun AddTaskDialog(onDismissRequest: () -> Unit, taskList: TaskList) {
    var name by remember { mutableStateOf("My Task") }
    var desc by remember { mutableStateOf("My Description") }
    var dueDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        )
    }
    val tags = remember { mutableStateListOf<String>() }
    var priority by remember { mutableIntStateOf(0) }
    var tagEnter by remember { mutableStateOf("") }
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
                    onValueChange = { priority = it.toInt() },
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
                    for (tag in tags) {
                        AssistChip(
                            label = { Text(text = tag) },
                            onClick = { tags.remove(tag) },
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
                        tags.add(tagEnter)
                        tagEnter = ""
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
                        UserTask.new {
                            this.task = name
                            this.description = desc
                            this.dueDate = LocalDate.parse(dueDate)
                            this.priority = priority.toString().toInt()
                            this.taskListId = taskList
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