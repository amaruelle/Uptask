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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.TaskList

@Composable
fun TaskListRow(taskList: TaskList, navController: NavController, userId: Int) {
    val taskListId = transaction {
        taskList.id.value
    }
    Row(
        Modifier
            .padding(12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth()
            .clickable {
                navController.navigate("task/$userId/$taskListId")
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
            navController.navigate("modifyTaskList/$taskListId")
        }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings for task list",
            )
        }
        IconButton(onClick = { navController.navigate("task/$userId/$taskListId") }) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = "Arrow to proceed to task list",
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.inverseOnSurface,
                    shape = CircleShape
                )
            )
        }
    }
}