package org.leftbrained.uptaskapp.nav

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.connectToDb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserActivity(navController: NavController, userId: Int) {
    connectToDb()
    val activity = LocalContext.current as Activity
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val userName = transaction {
        val user = User.findById(userId)
        user?.login ?: "No Login"
    }
    var userNameEdit by remember { mutableStateOf(userName) }
    var password by remember { mutableStateOf("") }
    var currentPass by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Uptask", style = MaterialTheme.typography.titleLarge)
                        Text(text = "Profile", style = MaterialTheme.typography.labelMedium)
                    }
                },
                navigationIcon = {
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
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "Here you can change your profile information",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = userNameEdit,
                    onValueChange = { userNameEdit = it },
                    label = { Text("Login") }
                )
                OutlinedTextField(
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    label = { Text("Current Password") }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New Password") }
                )
                Button(
                    onClick = {
                        transaction {
                            val user = User.findById(userId)
                            println(
                                "User: ${user?.login}, ${user?.password}, $currentPass, $password"
                            )
                            if (user?.password == currentPass) {
                                if (password.isNotEmpty() && password != currentPass && userName.isNotEmpty()) {
                                    user.login = userNameEdit
                                    user.password = password
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Please fill all fields correctly",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    navController.context,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
                Button(
                    onClick = {
                        transaction {
                            UptaskDb.Users.deleteAll()
                            UptaskDb.UserTasks.deleteAll()
                            UptaskDb.TaskLists.deleteAll()
                            UptaskDb.TaskTags.deleteAll()
                            UptaskDb.Logs.deleteAll()
                        }
                        Toast.makeText(
                            navController.context,
                            "Database cleared. You will be logged out.",
                            Toast.LENGTH_SHORT
                        ).show()
                        with(sharedPref.edit()) {
                            putString("user", "0")
                            apply()
                        }
                        navController.navigate("auth")
                    }
                ) {
                    Text("Clear database")
                }
            }
        }
    }
}

@Preview(device = "id:pixel_8_pro", showSystemUi = true, showBackground = true)
@Composable
fun UserActivityPreview() {
    UserActivity(rememberNavController(), 1)
}