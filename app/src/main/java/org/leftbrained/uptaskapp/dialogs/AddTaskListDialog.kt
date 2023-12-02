package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.User
import org.leftbrained.uptaskapp.classes.connectToDb

@Composable
fun AddTaskListDialog(onDismissRequest: () -> Unit, userId: Int) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Add Task List",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enter values for new task list",
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji") },
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(onClick = {
                        connectToDb()
                        transaction {
                            TaskList.new { this.name = name; this.emoji = emoji; this.userId = User.findById(userId)!! }
                        }
                        onDismissRequest()
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = "Add")
                    }
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}