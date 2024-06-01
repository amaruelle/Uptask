package org.leftbrained.uptaskapp.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.db.User
import org.leftbrained.uptaskapp.db.connectToDb
import org.leftbrained.uptaskapp.ui.theme.AppTheme
import org.leftbrained.uptaskapp.viewmodel.UserViewModel

@Composable
fun RegisterActivity(navController: NavController) {
    connectToDb()
    val userVm = remember { UserViewModel() }
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
                stringResource(R.string.sign_up_to_continue),
                textAlign = TextAlign.Center,
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
                label = { Text(stringResource(R.string.login)) },
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
                label = { Text(stringResource(R.string.password)) },
                maxLines = 1
            )
            Button(onClick =
            {
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    connectToDb()
                    val usr = userVm.newUser(login, password)
                    val usrId = transaction { usr.id.value }
                    transaction {
                        userVm.setCurrentUser(
                            activity.getPreferences(Context.MODE_PRIVATE),
                            User.findById(usrId)!!
                        )
                    }
                    navController.navigate("taskList/${usrId}")
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_fill_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            ) {
                Text(text = stringResource(R.string.sign_up))
                Spacer(Modifier.size(8.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, "Arrow forward icon")
            }
            Text(
                stringResource(R.string.want_to_sign_in_instead),
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = {
                navController.navigate("auth")
            }) {
                Text(text = stringResource(R.string.sign_in))
            }
        }
    }
}