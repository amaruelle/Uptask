package org.leftbrained.uptaskapp

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.Exporter
import org.leftbrained.uptaskapp.components.TaskListRow
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.connectToDb
import org.leftbrained.uptaskapp.dialogs.AddTaskListDialog
import org.leftbrained.uptaskapp.dialogs.SettingsDialog
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

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
    val context = LocalContext.current
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
                    Text(
                        text = stringResource(R.string.task_lists),
                        style = MaterialTheme.typography.labelMedium
                    )
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
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back icon"
                    )
                }
            }, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            actions = {
                IconButton(
                    onClick = {
                        navController.navigate("stats/$userId")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = "Stats icon"
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate(
                            "user/$userId"
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Profile tab"
                    )
                }
                IconButton(
                    onClick = {
                        val exporter = Exporter()
                        val exported =
                            transaction { exporter.dataToMarkdown(User.findById(userId)!!.login) }
                        val documentsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        val file = File(documentsDir, "uptask_export_data.txt")

                        val fileOutputStream = FileOutputStream(file)
                        val outputStreamWriter = OutputStreamWriter(fileOutputStream)

                        outputStreamWriter.write(exported)
                        outputStreamWriter.close()
                        Toast.makeText(
                            context,
                            "Data exported to file in the Documents folder",
                            Toast.LENGTH_LONG
                        ).show(
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download, contentDescription = "Download icon"
                    )
                }
            }
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