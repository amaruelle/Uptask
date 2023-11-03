package org.leftbrained.uptaskapp

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import org.leftbrained.uptaskapp.classes.Task
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.TaskListAll
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActivity(taskList: TaskList, navController: NavController) {
    var showSettings by remember { mutableStateOf(false) }
    var showAddTask by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = {
            Column {
                Text("Uptask", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${taskList.emoji} ${taskList.name}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigate("taskList")
            }) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = "Back"
                )
            }
        }, actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.Search, contentDescription = "Search"
                )
            }
            IconButton(onClick = { showFilter = !showFilter }) {
                Icon(
                    imageVector = Icons.Rounded.List, contentDescription = "Settings"
                )
            }
        }, colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        )
    }, bottomBar = {
        BottomAppBar(actions = {
            IconButton(onClick = { showSettings = !showSettings }) {
                Icon(
                    imageVector = Icons.Rounded.Settings, contentDescription = "Settings"
                )
            }
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTask = !showAddTask },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Add, "Localized description")
            }
        })
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            for (task in taskList.tasks) {
                TaskView(task, taskList)
            }
            if (showSettings) {
                SettingsActivity(navController = navController) { showSettings = false }
            }
            if (showAddTask) {
                AddTaskDialog(onDismissRequest = { showAddTask = false }, taskList)
            }
            if (showFilter) {
                FilterSortDialog(onDismissRequest = { showFilter = false }, taskList)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskView(task: Task, taskList: TaskList) {
    var isChecked by remember { mutableStateOf(task.isCompleted) }
    Row(
        Modifier
            .padding(12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = {
            isChecked = !isChecked
            task.isCompleted = !task.isCompleted
            if (task.isCompleted) {
                taskList.tasks.remove(task)
            }
        })
        Column(Modifier.padding(12.dp)) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.DateRange,
                    contentDescription = "Date range icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = task.dueDate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row {
                for (tag in task.tags) {
                    AssistChip(
                        label = { Text(text = tag) },
                        onClick = { task.tags.remove(tag) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings for task",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismissRequest: () -> Unit, taskList: TaskList) {
    var name by remember { mutableStateOf("My Task") }
    var desc by remember { mutableStateOf("My Description") }
    var dueDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        )
    }
    var tags by remember { mutableStateOf(mutableStateListOf<String>()) }
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
                    text = "Add Task List",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enter values for new task list",
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
                        taskList.add(Task(false, name, desc, dueDate, tags, 0))
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

@Preview(device = "id:pixel_7_pro")
@Composable
fun TaskActivityPreview() {
    AppTheme {
        TaskActivity(taskList = TaskList(), rememberNavController())
    }
}