package org.leftbrained.uptaskapp.dialogs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Log
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UserTask
import java.time.ZoneOffset

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
    val context = LocalContext.current
    var priority by remember { mutableIntStateOf(0) }
    var tagEnter by remember { mutableStateOf("") }
    val userId = transaction { taskList.userId }
    val taskTags by remember(vm.databaseState) {
        derivedStateOf {
            listOf<Tag>()
        }
    }
    var attachment by remember {
        mutableStateOf("")
    }
    val sharedPref = (LocalContext.current as Activity).getPreferences(Context.MODE_PRIVATE)
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
                            if (tagEnter == "") {
                                Toast.makeText(
                                    context,
                                    "Tag can't be empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@IconButton
                            }
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
                Row{
                    AssistChip(onClick = {
                        val chooseFile = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
                        context.startActivity(Intent.createChooser(chooseFile, "Choose a file"))
                        val path = chooseFile.data
                        attachment = path.toString()

                    }, label = { Text("Pick attachment") })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        val dateRegex = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}")
                        if (!dueDate.matches(dateRegex)) {
                            Toast.makeText(
                                context,
                                "Date doesn't match the pattern",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        if (name == "" || desc == "" || !priority.toString().matches(Regex("[0-5]"))) {
                            Toast.makeText(
                                context,
                                "Invalid parameters",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        transaction {
                            UserTask.new {
                                this.task = name
                                this.description = desc
                                this.dueDate = LocalDate.parse(dueDate)
                                this.priority = priority.toString().toInt()
                                this.taskListId = taskList
                                this.isDone = false
                                this.userId = userId
                                this.attachment = attachment
                            }
                            val newTaskId = UserTask.all().last()
                            for (tag in tags) {
                                val newTag = Tag[tag.id.value]
                                newTag.taskId = newTaskId
                            }
                            vm.databaseState++
//                            Log.new {
//                                this.userId = userId
//                                this.action = "Add"
//                                this.date = LocalDate.parse(
//                                    Clock.System.now()
//                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
//                                )
//                                this.taskId = newTaskId
//                            }
                            // Add log to shared prefs
                            with(sharedPref.edit()) {
                                val logs = sharedPref.getStringSet("logs", mutableSetOf())
                                val newLogs = logs?.toMutableSet()
                                newLogs?.add(
                                    "${logs.size + 1} - ${LocalDate.parse(
                                        Clock.System.now()
                                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                                    )} - ${userId.id.value} - Add - ${newTaskId.id.value}"
                                )
                                putStringSet("logs", newLogs)
                                apply()
                            }
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