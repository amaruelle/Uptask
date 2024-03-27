package org.leftbrained.uptaskapp

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.Stats
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.UserTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsActivity(navController: NavController, userId: Int) {
    val sharedPref = (LocalContext.current as Activity).getPreferences(Context.MODE_PRIVATE)
    val pickerState = rememberDateRangePickerState()
    var currentStats by remember { mutableStateOf(Stats(userId, 0, 0, 0)) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stats") }, navigationIcon = {
                IconButton(
                    onClick = {
                        navController.navigate("taskList/$userId")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "Back icon"
                    )
                }
            }, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier
                    .height(500.dp)
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                DateRangePicker(
                    state = pickerState
                )
            }
            Button(
                onClick = {
                    val startDate =
                        Instant.fromEpochMilliseconds(pickerState.selectedStartDateMillis!!)
                            .toLocalDateTime(
                                TimeZone.currentSystemDefault()
                            )
                    val endDate = Instant.fromEpochMilliseconds(pickerState.selectedEndDateMillis!!)
                        .toLocalDateTime(
                            TimeZone.currentSystemDefault()
                        )
                    with(sharedPref.edit()) {
                        val logs = sharedPref.getStringSet("logs", mutableSetOf())
                        val totalTasks = logs?.count {
                            val date = LocalDate.parse(it.split(" - ")[1])
                            val action = it.split(" - ")[3]
                            date in startDate.date..endDate.date && action == "Add" && userId == it.split(" - ")[2].toInt()
                        } ?: 0
                        val completedTasks = logs?.count {
                            val date = LocalDate.parse(it.split(" - ")[1])
                            val action = it.split(" - ")[3]
                            date in startDate.date..endDate.date && action == "Done" && userId == it.split(" - ")[2].toInt()
                        } ?: 0
                        val undoneTasks = logs?.count {
                            val date = LocalDate.parse(it.split(" - ")[1])
                            val action = it.split(" - ")[3]
                            date in startDate.date..endDate.date && action == "Undone" && userId == it.split(" - ")[2].toInt()
                        } ?: 0
                        currentStats = Stats(userId, totalTasks, completedTasks, undoneTasks)
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Apply")
            }

            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = "Stats for the period",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Total tasks added: ${currentStats.totalTasks}"
            )
            Text(
                "Total tasks completed: ${currentStats.completedTasks}"
            )
            Text(
                "Total tasks undone: ${currentStats.undoneTasks}"
            )
        }
    }
}

//fun getStats(userId: Int, startDate: LocalDate, endDate: LocalDate): Stats {
//    return transaction {
//        val completedTasksCount = UserTask.find {
//            (UptaskDb.UserTasks.userId eq userId) and
//                    (UptaskDb.Logs.date greaterEq startDate) and
//                    (UptaskDb.Logs.date lessEq endDate) and
//                    (UptaskDb.Logs.action eq "Done")
//        }.count()
//
//        val incompleteTasksCount = UserTask.find {
//            (UptaskDb.UserTasks.userId eq userId) and
//                    (UptaskDb.Logs.date greaterEq startDate) and
//                    (UptaskDb.Logs.date lessEq endDate) and
//                    (UptaskDb.Logs.action eq "Undone")
//        }.count()
//
//        val totalTasksCount = UserTask.find {
//            (UptaskDb.UserTasks.userId eq userId) and
//                    (UptaskDb.Logs.date greaterEq startDate) and
//                    (UptaskDb.Logs.date lessEq endDate) and
//                    (UptaskDb.Logs.action eq "Add")
//        }.count()
//
//        val newStats = Stats(userId, totalTasksCount, completedTasksCount, incompleteTasksCount)
//        newStats
//    }
//}

@Preview(showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun StatsActivityPreview() {
    val navController = rememberNavController()
    StatsActivity(navController, 1)
}