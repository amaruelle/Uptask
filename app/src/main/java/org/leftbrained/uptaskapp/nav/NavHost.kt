package org.leftbrained.uptaskapp.nav

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.*
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.connectToDb
import org.leftbrained.uptaskapp.dialogs.ModifyTaskDialog
import org.leftbrained.uptaskapp.dialogs.ModifyTaskListDialog

@Composable
fun GeneralNav() {
    val navController = rememberNavController()

    val activity = LocalContext.current as Activity
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val checkVal = sharedPref.getBoolean("isFirstTime", true)
    val start = remember {
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
        }
    }
    connectToDb()
    LaunchedEffect(null) {
        println(start)
        transaction {
            if (SchemaUtils.listTables().size > 15) return@transaction
            SchemaUtils.create(UptaskDb.UserTasks, UptaskDb.Users, UptaskDb.TaskLists, UptaskDb.TaskTags, UptaskDb.Logs)
        }
    }

    NavHost(navController = navController, startDestination = start) {
        composable("main") {
            WelcomeScreen(navController)
        }
        composable("taskList/{user}", arguments = listOf(navArgument("user") { type = NavType.IntType })) {
            LaunchedEffect(null) {
                println(it.arguments?.getInt("user"))
            }
            TaskListActivity(
                navController = navController,
                userId = it.arguments?.getInt("user")!!
            )
        }
        composable(
            "task/{user}/{listId}/{sort}/{filter}/{showDone}",
            arguments = listOf(navArgument("user") { type = NavType.IntType },
                navArgument("listId") { type = NavType.IntType }, navArgument("sort") { type = NavType.IntType },
                navArgument("filter") { type = NavType.StringType }, navArgument("showDone") { type = NavType.BoolType }
            )
        ) {
            TaskActivity(
                taskListId = it.arguments?.getInt("listId")!!,
                navController = navController,
                userId = it.arguments?.getInt("user")!!,
                sort = it.arguments?.getInt("sort")!!,
                filter = it.arguments?.getString("filter")!!,
                showDone = it.arguments?.getBoolean("showDone")!!
            )
        }
        dialog("modifyTask/{task}", arguments = listOf(navArgument("task") { type = NavType.IntType })) {
            val task = it.arguments?.getInt("task")
            ModifyTaskDialog(
                onDismissRequest = {
                    navController.popBackStack()
                }, taskId = task!!
            )
        }
        dialog("modifyTaskList/{taskList}", arguments = listOf(navArgument("taskList") { type = NavType.IntType })) {
            val taskList = it.arguments?.getInt("taskList")
            ModifyTaskListDialog(
                onDismissRequest = {
                    navController.popBackStack()
                }, taskListId = taskList!!
            )
        }
        composable("auth") {
            AuthActivity(navController)
        }
        composable("register") {
            RegisterActivity(navController)
        }
        composable("stats/{user}", arguments = listOf(navArgument("user") { type = NavType.IntType })) {
            StatsActivity(navController, it.arguments?.getInt("user")!!)
        }
        composable("user/{user}", arguments = listOf(navArgument("user") { type = NavType.IntType })) {
            UserActivity(navController, it.arguments?.getInt("user")!!)
        }
    }
    LaunchedEffect(null) {
        navController.navigate(start)
    }
}
