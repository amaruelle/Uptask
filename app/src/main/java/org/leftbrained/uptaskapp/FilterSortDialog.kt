package org.leftbrained.uptaskapp

import android.widget.RadioGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.leftbrained.uptaskapp.classes.TaskList

@Composable
fun FilterSortDialog(onDismissRequest: () -> Unit, taskList: TaskList) {
    var sortName by remember { mutableStateOf(false) }
    var sortDate by remember { mutableStateOf(false) }
    var sortPriority by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Filter and sort",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Sort by", style = MaterialTheme.typography.bodyMedium)
                Column(Modifier.selectableGroup()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortName, onClick = { sortName = !sortName })
                        Text("Name")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortDate, onClick = { sortDate = !sortDate })
                        Text("Date")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortPriority, onClick = { sortPriority = !sortPriority })
                        Text("Priority")
                    }
                }
                Button(onClick = {
                    if (sortName) {
                        taskList.tasks.sortBy { it.name }
                    }
                    if (sortDate) {
                        taskList.tasks.sortBy { it.dueDate }
                    }
                    if (sortPriority) {
                        taskList.tasks.sortBy { it.priority }
                    }
                }) {
                    Text("Apply")
                }
            }
        }
    }
}