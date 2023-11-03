package org.leftbrained.uptaskapp

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.leftbrained.uptaskapp.classes.TaskList
import org.leftbrained.uptaskapp.nav.GeneralNav
import org.leftbrained.uptaskapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var host = GeneralNav()
                    val sharedPref: SharedPreferences =
                        getPreferences(MODE_PRIVATE) ?: return@Surface
                    val hasVisited = sharedPref.getBoolean("hasVisited", false)
                    if (!hasVisited) {
                        with(sharedPref.edit()) {
                            putBoolean("hasVisited", true)
                            apply()
                            host.navigate("main")
                        }
                    } else {
                        host.navigate("taskList")
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController) {
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
                    text = "Welcome to Uptask",
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Organize your tasks",
                    modifier = Modifier.padding(top = 8.dp),
                )
                Button(
                    onClick = { navController.navigate("taskList") },
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play arrow icon"
                    )
                    Text(text = "Get Started")
                }
            }
        }
        Column(Modifier.padding(top = 48.dp)) {
            Text(
                text = "Here's what you can do with Uptask",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(24.dp))
            UnorderedList("Create/edit/remove task lists")
            UnorderedList("Add/delete/edit tasks from your lists")
            UnorderedList("Add tags and due dates to your tasks")
        }
    }
}

@Composable
fun UnorderedList(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = "Check circle icon",
            modifier = Modifier
                .size(28.dp)
                .padding(end = 8.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(
    showBackground = true, device = "id:pixel_7_pro", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
fun GreetingPreview() {
    AppTheme {
        WelcomeScreen(rememberNavController())
    }
}