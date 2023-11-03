package org.leftbrained.uptaskapp.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.leftbrained.uptaskapp.TaskActivity
import org.leftbrained.uptaskapp.TaskListScreen
import org.leftbrained.uptaskapp.WelcomeScreen
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.classes.TaskListAll

@Composable
fun GeneralNav(): NavHostController {
    val navController = rememberNavController()
    var tasks = TaskListAll(listOf(TaskList(), TaskList()))

    NavHost(navController = navController, startDestination = "main") {
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
    }
    return navController
}