package org.leftbrained.uptaskapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.TaskListAll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortDialog(
    onDismissRequest: () -> Unit,
    taskList: TaskList? = null,
    taskLists: TaskListAll? = null
) {
    var sortName by remember { mutableStateOf(false) }
    var sortDate by remember { mutableStateOf(false) }
    var sortPriority by remember { mutableStateOf(false) }
    var filterQuery by remember { mutableStateOf("") }
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
                    if (taskList != null) {
                        if (sortName) {
                            taskList.tasks.sortBy { it.name }
                        }
                        if (sortDate) {
                            taskList.tasks.sortBy { it.dueDate }
                        }
                        if (sortPriority) {
                            taskList.tasks.sortBy { it.priority }
                        }
                    }
                    if (taskLists != null) {
                        if (sortName) {
                            taskLists.list.sortBy { it.name }
                        }
                        if (sortDate) {
                            taskLists.list.sortBy { it.emoji }
                        }
                        if (sortPriority) {
                            taskLists.list.sortBy { it.tasks[0].priority }
                        }
                    }
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