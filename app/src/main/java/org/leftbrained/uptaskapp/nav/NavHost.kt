package org.leftbrained.uptaskapp.nav

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import org.leftbrained.uptaskapp.*
import org.leftbrained.uptaskapp.dialogs.ModifyTaskDialog
import org.leftbrained.uptaskapp.dialogs.ModifyTaskListDialog

@Composable
fun GeneralNav(): NavHostController {
    val navController = rememberNavController()

    val activity = LocalContext.current as Activity
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val checkVal = sharedPref.getBoolean("isFirstTime", true)

    //connectToDb()
    //transaction { SchemaUtils.create(UptaskDb.UserTasks, UptaskDb.Users, UptaskDb.TaskLists, UptaskDb.TaskTags) }

    NavHost(navController = navController, startDestination =
    if (checkVal) {
        with(sharedPref.edit()) {
            putBoolean("isFirstTime", false)
            apply()
            "main"
        }
    } else {
        val userId = sharedPref.getString("user", "0")
        if (userId == "0") {
            "auth"
        } else {
            "taskList/$userId"
        }
    }) {
        composable("main") {
            WelcomeScreen(navController)
        }
        composable("taskList/{user}") {
            TaskListActivity(
                navController = navController,
                userId = it.arguments?.getString("user")?.toInt() ?: 0
            )
        }
        composable(
            "task/{user}/{listId}"
        ) {
            TaskActivity(
                taskListId = it.arguments?.getString("listId")?.toInt() ?: 0,
                navController = navController,
                userId = it.arguments?.getString("user")?.toInt() ?: 0
            )
        }
        dialog("modifyTask/{task}") {
            val task = it.arguments?.getString("task")
            ModifyTaskDialog(
                onDismissRequest = {
                    navController.popBackStack()
                }, taskId = task?.toInt() ?: 0
            )
        }
        dialog("modifyTaskList/{taskList}") {
            val taskList = it.arguments?.getString("taskList")
            ModifyTaskListDialog(
                onDismissRequest = {
                    navController.popBackStack()
                }, taskListId = taskList?.toInt() ?: 0
            )
        }
        composable("auth") {
            AuthActivity(navController)
        }
        composable("register") {
            RegisterActivity(navController)
        }
    }
    return navController
}
