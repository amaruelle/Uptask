package org.leftbrained.uptaskapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.Exporter
import org.leftbrained.uptaskapp.components.TaskView
import org.leftbrained.uptaskapp.db.*
import org.leftbrained.uptaskapp.dialogs.AddTaskDialog
import org.leftbrained.uptaskapp.dialogs.FilterSortDialog
import org.leftbrained.uptaskapp.dialogs.SettingsDialog
import org.leftbrained.uptaskapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActivity(
    taskListId: Int,
    navController: NavController,
    vm: DatabaseStateViewmodel = viewModel(),
    userId: Int,
    sort: Int,
    filter: String,
    showDone: Boolean
) {
    var showSettings by remember { mutableStateOf(false) }
    var showAddTask by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    val taskList = transaction {
        TaskList.find { UptaskDb.TaskLists.id eq taskListId }.firstOrNull()
    }
    val tasks by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                UserTask.find {
                    (UptaskDb.UserTasks.taskListId eq taskListId) and
                            (UptaskDb.UserTasks.isDone eq showDone)
                }.orderBy(
                    when (sort) {
                        1 -> UptaskDb.UserTasks.task to SortOrder.ASC
                        2 -> UptaskDb.UserTasks.priority to SortOrder.ASC
                        3 -> UptaskDb.UserTasks.dueDate to SortOrder.ASC
                        else -> UptaskDb.UserTasks.id to SortOrder.ASC
                    }
                ).filter {
                    when (filter) {
                        "none" -> it.task.contains("") || it.description?.contains("") ?: true
                        else -> it.task.contains(filter) || it.description?.contains(filter) ?: true
                    }
                }.toList()
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back icon"
                )
            }
        }, actions = {
            IconButton(onClick = { showFilter = !showFilter }) {
                Icon(
                    imageVector = Icons.Rounded.FilterAlt, contentDescription = "Filter icon"
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
            IconButton(onClick = {
                navController.navigate("task/$userId/$taskListId/$sort/$filter/${!showDone}")
            }) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = "Done checkmark"
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
            for (task in tasks) {
                val taskId = transaction {
                    task.id.value
                }
                TaskView(taskId)
            }
            if (showSettings) {
                SettingsDialog { showSettings = false }
            }
            if (showAddTask) {
                AddTaskDialog(onDismissRequest = { showAddTask = false }, taskList!!)
            }
            if (showFilter) {
                FilterSortDialog(
                    onDismissRequest = { showFilter = false },
                    navController,
                    taskListId
                )
            }
        }
    }
}