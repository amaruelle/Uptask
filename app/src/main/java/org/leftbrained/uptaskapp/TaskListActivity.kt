package org.leftbrained.uptaskapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.TaskListsViewmodel
import org.leftbrained.uptaskapp.classes.User
import org.leftbrained.uptaskapp.components.TaskListRow
import org.leftbrained.uptaskapp.dialogs.AddTaskListDialog
import org.leftbrained.uptaskapp.dialogs.SettingsDialog
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListActivity(userId: Int, navController: NavController) {
    val user = transaction {
        return@transaction User.findById(userId)
    }
    if (user == null) {
        navController.navigate("auth")
        return
    }
    val taskListsViewmodel = TaskListsViewmodel()
    var showAddDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Uptask", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Task Lists", style = MaterialTheme.typography.labelMedium)
                }
            },
            actions = {
                if (showSearch) {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        label = { Text("Search") },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(150.dp),
                        maxLines = 1
                    )
                }
                IconButton(onClick = { showSearch = !showSearch }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { showFilter = !showFilter }) {
                    Icon(
                        imageVector = Icons.Rounded.List,
                        contentDescription = "Settings"
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
                    Icon(Icons.Filled.Add, "Add icon")
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
            taskListsViewmodel.getTaskLists(userId).forEach {
                TaskListRow(it, navController)
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