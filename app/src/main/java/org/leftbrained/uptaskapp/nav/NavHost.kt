package org.leftbrained.uptaskapp.nav

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.leftbrained.uptaskapp.ModifyTaskDialog
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.TaskActivity
import org.leftbrained.uptaskapp.TaskListScreen
import org.leftbrained.uptaskapp.WelcomeScreen
import org.leftbrained.uptaskapp.classes.Task
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.TaskListAll
import java.lang.Boolean.getBoolean

@Composable
fun GeneralNav(): NavHostController {
    val navController = rememberNavController()
    var tasks = TaskListAll(mutableListOf(TaskList(), TaskList()))

    val activity = LocalContext.current as Activity
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val checkVal = sharedPref.getBoolean("isFirstTime", false)

    NavHost(navController = navController, startDestination =
    if (!checkVal) {
        with(sharedPref.edit()) {
            putBoolean("isFirstTime", true)
            apply()
            "main"
        }
    } else {
        "taskList"
    }) {
        composable("main") {
            WelcomeScreen(navController)
        }
        composable("taskList") {
            TaskListScreen(tasks, navController)
        }
        composable(
            "task/{task}",
            arguments = listOf(navArgument("task") { type = NavType.StringType })
        ) {
            TaskActivity(
                taskList = Json.decodeFromString<TaskList>(
                    it.arguments?.getString("task") ?: "{}"
                ), navController = navController
            )
        }
        dialog("modifyTask/{task}") {
            val task = Json.decodeFromString<Task>(it.arguments?.getString("task") ?: "{}")
            ModifyTaskDialog(onDismissRequest = {
                navController.popBackStack()
            }, task = task)
        }
    }
    return navController
}
