package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.*

@Composable
fun FilterSortDialog(
    onDismissRequest: () -> Unit,
    vm: DatabaseStateViewmodel = viewModel(),
    taskListId: Int? = null
) {
    var sortName by remember { mutableStateOf(false) }
    var sortDate by remember { mutableStateOf(false) }
    var sortPriority by remember { mutableStateOf(false) }
    var filterQuery by remember { mutableStateOf("") }
    val taskListsViewmodel = remember { TaskListsViewmodel() }
    val tasksViewmodel = remember { TasksViewmodel() }
    val taskList = remember { taskListId?.let { taskListsViewmodel.getTaskList(it) } }
    val tasks by remember { mutableStateOf(TasksViewmodel().getTasks(taskListId!!)) }
    val sortedTasks = if (sortName) {
        transaction {
            UptaskDb.UserTasks.selectAll()
                .orderBy(UptaskDb.UserTasks.columns.find { it.name == "TASK" }!! to SortOrder.ASC)
        }
    } else if (sortDate) {
        transaction {
            UptaskDb.UserTasks.selectAll()
                .orderBy(UptaskDb.UserTasks.columns.find { it.name == "dueDate" }!! to SortOrder.ASC)
        }
    } else if (sortPriority) {
        transaction {
            UptaskDb.UserTasks.selectAll()
                .orderBy(UptaskDb.UserTasks.columns.find { it.name == "PRIORITY" }!! to SortOrder.ASC)
        }
    } else tasks
    LaunchedEffect(
        sortName,
        sortDate,
        sortPriority
    ) {
        if (sortName) {
            vm.sortingCriteria = SortingCriteria.Name
            vm.databaseState++
        } else if (sortDate) {
            vm.sortingCriteria = SortingCriteria.Date
            vm.databaseState++
        } else if (sortPriority) {
            vm.sortingCriteria = SortingCriteria.Priority
            vm.databaseState++
        } else {
            vm.sortingCriteria = SortingCriteria.None
            vm.databaseState++
        }
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Filter and sort",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Sort by", style = MaterialTheme.typography.labelLarge)
                Column(Modifier.selectableGroup()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortName, onClick = { sortName = !sortName })
                        Text("Name")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortDate, onClick = { sortDate = !sortDate })
                        Text("Date or Emoji")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = sortPriority,
                            onClick = { sortPriority = !sortPriority })
                        Text("Priority")
                    }
                }
                Text("Filter by", style = MaterialTheme.typography.labelLarge)
                Column(Modifier.selectableGroup()) {
                    OutlinedTextField(
                        value = filterQuery,
                        onValueChange = { filterQuery = it },
                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
                        label = { Text("Search") })
                }
                Button(onClick = {
                    // Callback
                    onDismissRequest()
                }) {
                    Text("Apply")
                }
            }
        }
    }
}

@Preview
@Composable
fun FilterSortDialogPreview() {
    FilterSortDialog(onDismissRequest = {})
}