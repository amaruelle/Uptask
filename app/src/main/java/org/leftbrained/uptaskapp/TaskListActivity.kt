package org.leftbrained.uptaskapp

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.SortingCriteria
import org.leftbrained.uptaskapp.components.TaskListRow
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.connectToDb
import org.leftbrained.uptaskapp.dialogs.AddTaskListDialog
import org.leftbrained.uptaskapp.dialogs.FilterSortDialog
import org.leftbrained.uptaskapp.dialogs.SettingsDialog
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListActivity(
    userId: Int,
    navController: NavController,
    vm: DatabaseStateViewmodel = viewModel()
) {
    connectToDb()
    LaunchedEffect(null) {
        println(userId)
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val activity = LocalContext.current as Activity
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val taskLists by remember(vm.databaseState) {
        derivedStateOf {
            transaction {
                TaskList.find { UptaskDb.TaskLists.userId eq userId }.toList()
            }
        }
    }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Uptask", style = MaterialTheme.typography.titleLarge)
                    Text(text = stringResource(R.string.task_lists), style = MaterialTheme.typography.labelMedium)
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.navigate("auth")
                        with(sharedPref.edit()) {
                            putString("user", "0")
                            apply()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "Back icon"
                    )
                }
            }, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }, bottomBar = {
        BottomAppBar(
            actions = {
                IconButton(onClick = { showSettings = !showSettings }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = !showAddDialog },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Rounded.Add, "Add icon")
                }
            }
        )
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            taskLists.forEach {
                TaskListRow(it, navController, userId)
            }
            if (showAddDialog) {
                AddTaskListDialog(onDismissRequest = { showAddDialog = false }, userId)
            }
            if (showSettings) {
                SettingsDialog { showSettings = false }
            }
        }
    }
}

@Preview(device = "id:pixel_7_pro", showSystemUi = true, showBackground = true)
@Composable
fun TaskListActivityPreview() {
    AppTheme {
        TaskListActivity(
            userId = 1,
            navController = rememberNavController()
        )
    }
}