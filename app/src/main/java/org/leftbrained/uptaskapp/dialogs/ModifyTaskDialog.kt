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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.LocalDate
import org.leftbrained.uptaskapp.classes.Tag
import org.leftbrained.uptaskapp.classes.TagsViewmodel
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel

@Composable
fun ModifyTaskDialog(onDismissRequest: () -> Unit, taskId: Int) {
    val vm: TaskViewModel = viewModel()
    val task = vm.getTaskById(taskId)
    var name by remember { mutableStateOf(task.task) }
    var desc by remember { mutableStateOf(task.description) }
    var dueDate = remember {
        task.dueDate.toString()
    }
    var priority by remember { mutableIntStateOf(task.priority) }
    var tagEnter by remember { mutableStateOf("") }
    val taskTags = TagsViewmodel().getTags(taskId)
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
                    for (tag in taskTags) {
                        AssistChip(
                            label = { Text(text = tag.tag) },
                            onClick = { tag.delete() },
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
                        TagsViewmodel().addTag(Tag.new { tag = tagEnter })
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
                        task.task = name
                        task.description = desc
                        task.dueDate = LocalDate.parse(dueDate)
                        task.priority = priority.toString().toInt()
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
                }
            }
        }
    }
}