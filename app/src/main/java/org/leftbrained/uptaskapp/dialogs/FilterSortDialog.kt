package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask

@Composable
fun FilterSortDialog(
    onDismissRequest: () -> Unit,
    navController: NavController,
    taskListId: Int
) {
    var sortName by remember { mutableStateOf(false) }
    var sortDate by remember { mutableStateOf(false) }
    var sortPriority by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val userId = transaction {
        UserTask.find { UptaskDb.UserTasks.taskListId eq taskListId }.firstOrNull()?.userId
    }!!.id.value
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.filter_and_sort),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.sort_by), style = MaterialTheme.typography.labelLarge)
                Column(Modifier.selectableGroup()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortName, onClick = { sortName = !sortName })
                        Text(stringResource(R.string.name))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortDate, onClick = { sortDate = !sortDate })
                        Text(stringResource(R.string.date_or_emoji))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = sortPriority,
                            onClick = { sortPriority = !sortPriority })
                        Text(stringResource(R.string.priority))
                    }
                }
                Text(stringResource(R.string.filter_by), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = search,
                    onValueChange = {
                        if (it.length > 48) return@OutlinedTextField
                        search = it
                    },
                    label = { Text(stringResource(R.string.search)) },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                )
                Button(
                    onClick = {
                        if (search == "") search = "none"
                        navController.navigate(
                            "task/$userId/$taskListId/${
                                when {
                                    sortName -> 1
                                    sortPriority -> 2
                                    sortDate -> 3
                                    else -> 0
                                }
                            }/$search/false"
                        )
                        onDismissRequest()
                    }
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Preview
@Composable
fun FilterSortDialogPreview() {
    FilterSortDialog(onDismissRequest = {}, rememberNavController(), 1)
}