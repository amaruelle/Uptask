package org.leftbrained.uptaskapp.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.classes.Exporter
import org.leftbrained.uptaskapp.components.TaskListRow
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.connectToDb
import org.leftbrained.uptaskapp.ui.dialogs.AddTaskListDialog
import org.leftbrained.uptaskapp.ui.dialogs.SettingsDialog

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
    var showExported by remember { mutableStateOf(false) }
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
                        transaction {
                            navController.navigate(
                                "user/$userId"
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Profile tab"
                    )
                }
                if (showExported) {
                    Dialog(onDismissRequest = { showExported = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            val scrollState = rememberScrollState()
                            val exportedData = transaction {
                                Exporter().dataToMarkdown(User.findById(userId)!!.login)
                            }
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .verticalScroll(scrollState)
                            ) {
                                Text(
                                    text = stringResource(R.string.here_is_your_export),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .padding(bottom = 16.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(R.string.click_copy),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                    textAlign = TextAlign.Center,
                                )
                                OutlinedTextField(
                                    value = exportedData,
                                    enabled = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {},
                                    singleLine = true,
                                    label = { Text("Exported data") }
                                )
                                Row(
                                    Modifier.padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TextButton(onClick = {
                                        val clipboardManager = context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager
                                        clipboardManager.setPrimaryClip(
                                            ClipData.newPlainText(
                                                "Exported data",
                                                exportedData
                                            )
                                        )
                                        showExported = false
                                    }) {
                                        Text(stringResource(R.string.copy))
                                    }
                                    TextButton(onClick = {
                                        showExported = false
                                    }) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                }
                            }
                        }
                    }
                }
                IconButton(
                    onClick = {
                        showExported = true
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