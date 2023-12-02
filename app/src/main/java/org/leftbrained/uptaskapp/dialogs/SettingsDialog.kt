package org.leftbrained.uptaskapp.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@Composable
fun SettingsDialog(onDismissRequest: () -> Unit) {
    var themeSwitch by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.width(200.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Settings", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Theme", style = MaterialTheme.typography.titleMedium)
                Switch(checked = themeSwitch, onCheckedChange = { themeSwitch = !themeSwitch })
                Button(onClick = { onDismissRequest() }) {
                    Text("Save")
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsDialogPreview() {
    AppTheme {
        var showSettings by remember { mutableStateOf(false) }
        SettingsDialog {
            showSettings = false
        }
    }
}