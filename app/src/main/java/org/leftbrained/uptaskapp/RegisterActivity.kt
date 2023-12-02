package org.leftbrained.uptaskapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.UptaskDb
import org.leftbrained.uptaskapp.classes.User
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@Composable
fun RegisterActivity(navController: NavController) {
    var login by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var isClearedLogin by remember {
        mutableStateOf(false)
    }
    var isClearedPassword by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val activity = context.findActivity()
    AppTheme {
        Column(
            Modifier
                .padding(16.dp)
                .padding(top = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.AccountCircle,
                contentDescription = "Auth account icon",
                Modifier
                    .size(64.dp)
                    .padding(bottom = 24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Sign up to continue",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                leadingIcon = { Icon(Icons.Rounded.Email, "Email icon") },
                trailingIcon = {
                    IconButton(
                        onClick = { login = "" },
                        enabled = isClearedLogin
                    ) { Icon(Icons.Rounded.Clear, "Clear icon") }
                },
                value = login,
                onValueChange = {
                    login = it
                    isClearedLogin = it.isNotEmpty()
                },
                label = { Text("Login") },
                maxLines = 1
            )
            OutlinedTextField(
                leadingIcon = { Icon(Icons.Rounded.Lock, "Password icon") },
                trailingIcon = {
                    IconButton(
                        onClick = { password = "" },
                        enabled = isClearedPassword
                    ) { Icon(Icons.Rounded.Clear, "Clear icon") }
                },
                value = password,
                onValueChange = {
                    password = it
                    isClearedPassword = it.isNotEmpty()
                },
                label = { Text("Password") },
                maxLines = 1
            )
            Button(onClick =
            {
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    Database.connect("jdbc:h2:file:/data/data/org.leftbrained.uptaskapp/databases/uptask.db", driver = "org.h2.Driver")
                    transaction {
                        User.new { this.login = login; this.password = password }
                    }
                    val userId = transaction {
                        User.find {
                            UptaskDb.Users.login eq login
                        }.first().id.value
                    }
                    with(activity.getPreferences(Context.MODE_PRIVATE)?.edit()) {
                        this?.putString("user", userId.toString())
                        this?.apply()
                    }
                    navController.navigate("taskList/${userId}")
                } else {
                    Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            ) {
                Text(text = "Sign up")
                Spacer(Modifier.size(8.dp))
                Icon(Icons.Rounded.ArrowForward, "Arrow forward icon")
            }
            Text(
                "Want to sign in instead?",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = {
                navController.navigate("auth")
            }) {
                Text(text = "Sign in")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterActivityPreview() {
    RegisterActivity(navController = NavController(LocalContext.current))
}