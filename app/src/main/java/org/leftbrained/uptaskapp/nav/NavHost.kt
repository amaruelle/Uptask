package org.leftbrained.uptaskapp.nav

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.leftbrained.uptaskapp.AuthActivity
import org.leftbrained.uptaskapp.dialogs.ModifyTaskDialog
import org.leftbrained.uptaskapp.RegisterActivity
import org.leftbrained.uptaskapp.TaskActivity
import org.leftbrained.uptaskapp.TaskListActivity
import org.leftbrained.uptaskapp.WelcomeScreen

@Composable
fun GeneralNav(): NavHostController {
    val navController = rememberNavController()

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
        "main"
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
            "task/{listId}/{task}",
            arguments = listOf(navArgument("task") { type = NavType.StringType })
        ) {
            TaskActivity(
                taskListId = it.arguments?.getString("listId")?.toInt() ?: 0, navController = navController
            )
        }
        dialog("modifyTask/{task}") {
            val task = it.arguments?.getString("task")
            ModifyTaskDialog(onDismissRequest = {
                navController.popBackStack()
            }, taskId = task?.toInt() ?: 0)
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
