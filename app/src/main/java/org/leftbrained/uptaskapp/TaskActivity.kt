package org.leftbrained.uptaskapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.*
import org.leftbrained.uptaskapp.components.TaskView
import org.leftbrained.uptaskapp.db.*
import org.leftbrained.uptaskapp.dialogs.AddTaskDialog
import org.leftbrained.uptaskapp.dialogs.FilterSortDialog
import org.leftbrained.uptaskapp.dialogs.SettingsDialog
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActivity(taskListId: Int, navController: NavController, vm: DatabaseStateViewmodel = viewModel(), userId: Int) {
    var showSettings by remember { mutableStateOf(false) }
    var showAddTask by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val taskList = transaction {
        TaskList.find { UptaskDb.TaskLists.id eq taskListId }.firstOrNull()
    }
    val tasks by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                UserTask.find {
                    UptaskDb.UserTasks.taskListId eq taskListId
                }.toList()
            }
        }
    }
    val final by remember(vm.databaseState) {
        derivedStateOf {
            when (vm.sortingCriteria) {
                SortingCriteria.Name -> {
                    tasks.sortedBy { it.task }
                }

                SortingCriteria.Date -> {
                    tasks.sortedBy { it.dueDate }
                }

                SortingCriteria.Priority -> {
                    tasks.sortedBy { it.priority }
                }

                else -> tasks
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Column {
                Text("Uptask", style = MaterialTheme.typography.titleLarge)
                if (taskList != null) {
                    Text(
                        text = "${taskList.emoji} ${taskList.name}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigate("taskList/$userId")
            }) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = "Back icon"
                )
            }
        }, actions = {
            if (showSearch) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text(stringResource(R.string.search)) },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(150.dp),
                    maxLines = 1
                )
            }
            IconButton(onClick = { showSearch = !showSearch }) {
                Icon(
                    imageVector = Icons.Rounded.Search, contentDescription = "Search icon"
                )
            }
            IconButton(onClick = { showFilter = !showFilter }) {
                Icon(
                    imageVector = Icons.Rounded.List, contentDescription = "Filter icon"
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
                    imageVector = Icons.Rounded.Settings, contentDescription = "Settings icon"
                )
            }
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTask = !showAddTask },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Rounded.Add, "Add icon")
            }
        })
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            for (task in final) {
                val taskId = transaction {
                    task.id.value
                }
                TaskView(navController, taskId)
            }
            if (showSettings) {
                SettingsDialog { showSettings = false }
            }
            if (showAddTask) {
                AddTaskDialog(onDismissRequest = { showAddTask = false }, taskList!!)
            }
            if (showFilter) {
                FilterSortDialog(onDismissRequest = { showFilter = false })
            }
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp")
@Composable
fun TaskActivityPreview() {
    AppTheme {
        TaskActivity(TaskList.new {
            this.userId = User.findById(0)!!; this.emoji = "F"; this.name = "TaskList 1"
        }.id.value, rememberNavController(), userId = 0)
    }
}