package org.leftbrained.uptaskapp.dialogs

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.classes.AlarmReceiver
import org.leftbrained.uptaskapp.classes.Checks
import org.leftbrained.uptaskapp.classes.Checks.tagCheck
import org.leftbrained.uptaskapp.classes.Logs
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel
import java.time.Duration
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTaskDialog(
    onDismissRequest: () -> Unit,
    taskId: Int,
    vm: DatabaseStateViewmodel = viewModel()
) {
    val scrollDateState = rememberScrollState()
    val scrollState = rememberScrollState()
    val reminderOptions = listOf(
        "None" to Duration.ZERO,
        "5 minutes" to Duration.ofMinutes(5),
        "10 minutes" to Duration.ofMinutes(10),
        "15 minutes" to Duration.ofMinutes(15),
        "30 minutes" to Duration.ofMinutes(30),
        "1 hour" to Duration.ofHours(1),
        "2 hours" to Duration.ofHours(2),
        "1 day" to Duration.ofDays(1)
    )
    var selectedReminder by remember { mutableStateOf(reminderOptions[0]) }
    var showReminderDropdown by remember { mutableStateOf(false) }
    val logs = Logs(LocalContext.current.getSharedPreferences("logs", Context.MODE_PRIVATE))
    val taskVm = remember { TaskViewModel() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val task by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                UserTask.find { UptaskDb.UserTasks.id eq taskId }.elementAt(0)
            }
        }
    }
    val initialDate =
        if (task.dueDate != null) task.dueDate!!.toInstant(TimeZone.UTC)
            .toEpochMilliseconds() else Instant.fromEpochMilliseconds(
            Clock.System.now().epochSeconds
        ).toEpochMilliseconds()
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate
        )
    val timePickerState = rememberTimePickerState(
        if (task.dueDate == null) 0 else task.dueDate!!.time.hour,
        if (task.dueDate == null) 0 else task.dueDate!!.time.minute
    )
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showExpandableTags by remember { mutableStateOf(false) }
    var dueDate: LocalDateTime? by remember {
        mutableStateOf(
            Instant.fromEpochMilliseconds(
                initialDate
            ).toLocalDateTime(TimeZone.UTC)
        )
    }
    var dueTime by remember { mutableStateOf(if (task.dueDate?.time.toString() == "null") "No time selected" else task.dueDate?.time.toString()) }
    val tags = remember {
        mutableListOf<Tag>()
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
                    value = desc ?: "",
                    onValueChange = { desc = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.padding(top = 16.dp)
                )
                Slider(
                    value = priority.toFloat(),
                    onValueChange = { priority = it.toInt() },
                    valueRange = 0f..5f,
                    steps = 5,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text("Priority: $priority")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = {
                            showDatePicker = true
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = "Due date icon"
                            )
                            Text("Select due date")
                        }
                    }
                    TextButton(onClick = {
                        dueDate = null
                    }) {
                        Text(text = "Clear")
                    }
                }

                Text("${if (dueDate?.date == LocalDate.parse("1970-01-20") || dueDate?.date == null) "No date selected" else dueDate?.date}")
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            if (datePickerState.selectedDateMillis == null) return@DatePickerDialog
                            val hourInMillis = timePickerState.hour * 3600000
                            val minuteInMillis = timePickerState.minute * 60000
                            val totalMillis =
                                hourInMillis + minuteInMillis + datePickerState.selectedDateMillis!!
                            Instant.fromEpochMilliseconds(totalMillis).toLocalDateTime(TimeZone.UTC)
                            dueDate = Instant.fromEpochMilliseconds(totalMillis)
                                .toLocalDateTime(TimeZone.UTC)
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
                                Text("Confirm")
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = {
                            showTimePicker = true
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = "Due time icon"
                            )
                            Text("Select due time")
                        }
                    }
                    TextButton(onClick = {
                        dueTime = ""
                    }) {
                        Text(text = "Clear")
                    }
                }
                Text(
                    if (dueTime == "") "No time selected" else dueTime,
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
                                        Text("Confirm")
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
                        label = { Text(text = "Reminder") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showReminderDropdown)
                        },
                        colors = OutlinedTextFieldDefaults.colors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showReminderDropdown,
                        onDismissRequest = { showReminderDropdown = false }
                    ) {
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
                            if (tags.find { it.tag == tagEnter } != null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Tag already added"
                                    )
                                }
                                return@IconButton
                            }
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
                    Text("Click to select existing tags")
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
                        if (!Checks.emptyCheck(name, context)) return@Button
                        if (!Checks.priorityCheck(priority, context)) return@Button
                        if (dueDate != null) {
                            val hourInMillis = timePickerState.hour * 3600000
                            val minuteInMillis = timePickerState.minute * 60000
                            val totalMillis =
                                hourInMillis + minuteInMillis + dueDate!!.toInstant(TimeZone.UTC)
                                    .toEpochMilliseconds()
                            dueDate =
                                Instant.fromEpochMilliseconds(totalMillis)
                                    .toLocalDateTime(TimeZone.UTC)
                            println("Total millis selected: $totalMillis")
                        }
                        transaction {
                            task.task = name
                            task.description = desc
                            task.dueDate = dueDate
                            task.priority = priority
                        }
                        if (selectedReminder.second != Duration.ZERO) {
                            val alarmManager =
                                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            val intent = Intent(context, AlarmReceiver::class.java)
                            intent.putExtra("taskName", name)
                            intent.putExtra("taskDueDate", dueDate!!.let {
                                "${it.dayOfMonth} ${
                                    it.month.name.let { name ->
                                        name.substring(0, 1)
                                            .uppercase(Locale.ROOT) + name.substring(1)
                                            .lowercase(Locale.ROOT)

                                    }
                                }, ${it.year} ${if (it.hour != 0 && it.minute != 0) "${it.hour}:${it.minute}" else ""}"
                            })
                            val newTaskId = transaction { (UserTask.all().count() + 1).toInt() }
                            intent.putExtra("taskDesc", desc)
                            val pendingIntent = PendingIntent.getBroadcast(
                                context, newTaskId, intent,
                                PendingIntent.FLAG_IMMUTABLE
                            )
                            val reminderMillis =
                                dueDate!!.toInstant(TimeZone.currentSystemDefault())
                                    .toEpochMilliseconds() - selectedReminder.second.toMillis()
                            if (alarmManager.canScheduleExactAlarms()) {
                                alarmManager.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    reminderMillis,
                                    pendingIntent
                                )
                            }
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