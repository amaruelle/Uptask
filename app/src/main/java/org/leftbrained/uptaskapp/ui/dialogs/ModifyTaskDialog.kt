package org.leftbrained.uptaskapp.ui.dialogs

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.classes.Verifications
import org.leftbrained.uptaskapp.classes.Verifications.tagAddedCheck
import org.leftbrained.uptaskapp.classes.Verifications.tagCheck
import org.leftbrained.uptaskapp.classes.Verifications.tagExistsCheck
import org.leftbrained.uptaskapp.other.Logs
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask
import org.leftbrained.uptaskapp.reminders.ReminderUtils
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTaskDialog(
    onDismissRequest: () -> Unit, taskId: Int, vm: DatabaseStateViewmodel = viewModel()
) {
    val scrollDateState = rememberScrollState()
    val scrollState = rememberScrollState()
    val reminderOptions = listOf(
        stringResource(R.string.none) to Duration.ZERO,
        stringResource(R.string._5_minutes) to Duration.parse("5m"),
        stringResource(R.string._10_minutes) to Duration.parse("10m"),
        stringResource(R.string._15_minutes) to Duration.parse("15m"),
        stringResource(R.string._30_minutes) to Duration.parse("30m"),
        stringResource(R.string._1_hour) to Duration.parse("1h"),
        stringResource(R.string._2_hours) to Duration.parse("2h"),
        stringResource(R.string._4_hours) to Duration.parse("4h"),
        stringResource(R.string._8_hours) to Duration.parse("8h"),
        stringResource(R.string._1_day) to Duration.parse("1d")
    )
    var selectedReminder by remember { mutableStateOf(reminderOptions[0]) }
    var showReminderDropdown by remember { mutableStateOf(false) }
    val logs = Logs(LocalContext.current.getSharedPreferences("logs", Context.MODE_PRIVATE))
    val taskVm = remember { TaskViewModel() }
    val task by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                UserTask.find { UptaskDb.UserTasks.id eq taskId }.elementAt(0)
            }
        }
    }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showExpandableTags by remember { mutableStateOf(false) }
    var dueDate: Instant? by remember {
        mutableStateOf(
            null
        )
    }
    var dueTime by remember { mutableStateOf("") }
    val tags = remember {
        mutableListOf<String>()
    }
    val context = LocalContext.current
    var name by remember { mutableStateOf(task.task) }
    var desc by remember { mutableStateOf(task.description) }
    var priority by remember { mutableIntStateOf(task.priority!!) }
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
                .height(600.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
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
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc ?: "",
                    onValueChange = { desc = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                )
                Slider(
                    value = priority.toFloat(),
                    onValueChange = { priority = it.toInt() },
                    valueRange = 0f..5f,
                    steps = 5,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(stringResource(R.string.priority_label, priority))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showDatePicker = true
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = "Due date icon"
                            )
                            Text(stringResource(R.string.select_due_date))
                        }
                    }
                    TextButton(onClick = {
                        dueDate = null
                    }, modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.5f)) {
                        Text(stringResource(R.string.clear))
                    }
                }

                Text(
                    "${
                        dueDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.date ?: stringResource(
                            R.string.no_date_selected
                        )
                    }"
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            if (datePickerState.selectedDateMillis == null) return@DatePickerDialog
                            dueDate =
                                Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                        }) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .verticalScroll(scrollDateState)
                        ) {
                            DatePicker(
                                state = datePickerState
                            )
                            Button(onClick = {
                                showDatePicker = false
                            }) {
                                Text(stringResource(R.string.confirm))
                            }
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showTimePicker = true
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = "Due time icon"
                            )
                            Text(stringResource(R.string.select_due_time))
                        }
                    }
                    TextButton(onClick = {
                        dueTime = ""
                    }, modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.5f)) {
                        Text(stringResource(R.string.clear))
                    }
                }
                Text(
                    if (dueTime == "") stringResource(R.string.no_time_selected) else dueTime,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (showTimePicker) {
                    Dialog(onDismissRequest = { showTimePicker = false }) {
                        Card {
                            Column(Modifier.padding(16.dp)) {
                                TimePicker(state = timePickerState)
                                Row {
                                    Button(onClick = {
                                        dueTime =
                                            "${timePickerState.hour}:${timePickerState.minute}"
                                        showTimePicker = false
                                    }) {
                                        Text(stringResource(R.string.confirm))
                                    }
                                }
                            }
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = showReminderDropdown,
                    onExpandedChange = { showReminderDropdown = !showReminderDropdown }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedReminder.first,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.reminder)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showReminderDropdown)
                        },
                        colors = OutlinedTextFieldDefaults.colors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                    ExposedDropdownMenu(expanded = showReminderDropdown,
                        onDismissRequest = { showReminderDropdown = false }) {
                        reminderOptions.forEach { reminder ->
                            DropdownMenuItem(text = {
                                Text(reminder.first)
                            }, onClick = {
                                selectedReminder = reminder
                                showReminderDropdown = false
                            })
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (tag in taskTags) {
                        AssistChip(label = { Text(text = tag.tag) }, onClick = {
                            transaction {
                                tag.delete()
                            }
                            vm.databaseState++
                        }, modifier = Modifier.padding(end = 8.dp)
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
                    label = { Text(stringResource(R.string.tag)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (!tagCheck(tagEnter, context)) return@IconButton
                            if (!tagAddedCheck(tags, tagEnter, context)) return@IconButton
                            if (!tagExistsCheck(tagEnter, context)) return@IconButton
                            transaction {
                                Tag.new {
                                    tag = tagEnter
                                    this.taskId = UserTask[taskId]
                                }
                                tags.add(tagEnter)
                            }
                            tagEnter = ""
                            vm.databaseState++
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Add, contentDescription = "Add tag icon"
                            )
                        }
                    })
                Row(Modifier.padding(top = 12.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.pick_attachment)) },
                        leadingIcon = {
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
                    Text(stringResource(R.string.click_to_select_existing_tags))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
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
                                        tags.add(el.key)
                                        vm.databaseState++
                                    }, label = { Text(el.key) }, Modifier.padding(end = 4.dp)
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
                        if (!Verifications.emptyCheck(name, context)) return@Button
                        if (!Verifications.priorityCheck(priority, context)) return@Button
                        if (dueDate != null) {
                            dueDate =
                                dueDate!! + Duration.parse("${timePickerState.hour}h ${timePickerState.minute}m")
                            if (selectedReminder.second != Duration.ZERO) {
                                ReminderUtils.setReminder(
                                    context,
                                    name,
                                    desc ?: "None",
                                    dueDate,
                                    selectedReminder
                                )
                            }
                        }
                        transaction {
                            task.task = name
                            task.description = desc
                            task.dueDate = dueDate?.toLocalDateTime(TimeZone.UTC)
                            task.priority = priority
                        }
                        onDismissRequest()
                        vm.databaseState++
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.modify))
                    }
                    OutlinedButton(
                        onClick = { onDismissRequest() }, modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    IconButton(
                        onClick = {
                            taskVm.removeTask(taskId)
                            logs.deleteTaskLog(userId, task)
                            onDismissRequest()
                            vm.databaseState++
                        }, modifier = Modifier.width(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete, contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    }
}