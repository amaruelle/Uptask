package org.leftbrained.uptaskapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.DatabaseStateViewmodel
import org.leftbrained.uptaskapp.db.TaskList
import org.leftbrained.uptaskapp.dialogs.ModifyTaskListDialog

@Composable
fun TaskListRow(taskList: TaskList, navController: NavController, userId: Int, vm: DatabaseStateViewmodel = viewModel()) {
    val taskListId = transaction {
        taskList.id.value
    }
    var showEdit by remember(vm.databaseState) { mutableStateOf(false) }
    Row(
        Modifier
            .padding(12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth()
            .clickable {
                navController.navigate("task/$userId/$taskListId/0/none/false")
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = taskList.emoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = taskList.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {
            showEdit = true
        }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings for task list",
            )
        }
        IconButton(onClick = { navController.navigate("task/$userId/$taskListId/0/none/false") }) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = "Arrow to proceed to task list",
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.inverseOnSurface,
                    shape = CircleShape
                )
            )
        }
        if (showEdit) {
            ModifyTaskListDialog(onDismissRequest = { showEdit = false }, taskListId = taskListId)
        }
    }
}