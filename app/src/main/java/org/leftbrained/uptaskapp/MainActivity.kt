package org.leftbrained.uptaskapp

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.components.UnorderedList
import org.leftbrained.uptaskapp.db.UserTask
import org.leftbrained.uptaskapp.db.connectToDb
import org.leftbrained.uptaskapp.nav.GeneralNav
import org.leftbrained.uptaskapp.ui.theme.AppTheme
import org.leftbrained.uptaskapp.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    private val sharedViewModel: TaskViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeneralNav()
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController) {
    connectToDb()
//    transaction {
//        if (User.findById(1) == null) {
//            User.new {
//                login = "testLogin"
//                password = "testPassword"
//            }
//        }
//    }
//    transaction {
//        if (TaskList.findById(1) == null) {
//            TaskList.new {
//                userId = User.findById(1)!!
//                name = "testList"
//                emoji = "testEmoji"
//            }
//        }
//    }
    transaction {
//        val currentMoment: Instant = Clock.System.now()
//        val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
//        val localDate = datetimeInSystemZone.date
//        if (UserTask.findById(1) == null) {
//            UserTask.new {
//                userId = User.findById(1)!!
//                taskListId = TaskList.findById(1)!!
//                task = "testTask"
//                description = "testDescription"
//                dueDate = localDate
//                isDone = false
//                priority = 3
//            }
//        }
    }
    Column(
        Modifier.padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(Modifier.padding(24.dp)) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Check circle icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.welcome),
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.organize_your_tasks),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Button(
                    onClick = {
                        navController.navigate("auth")
                    },
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play arrow icon"
                    )
                    Text(text = stringResource(R.string.get_started))
                }
            }
        }
        Column(Modifier.padding(top = 48.dp)) {
            Text(
                text = stringResource(R.string.what_you_can_do),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(24.dp))
            UnorderedList(stringResource(R.string.create_edit_remove_task_lists))
            UnorderedList(stringResource(R.string.add_delete_edit_tasks_from_your_lists))
            UnorderedList(stringResource(R.string.add_tags_and_due_dates))
        }
    }
}

@Preview(
    showBackground = true, device = "id:pixel_7_pro", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
fun MainActivityPreview() {
    AppTheme {
        WelcomeScreen(rememberNavController())
    }
}