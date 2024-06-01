package org.leftbrained.uptaskapp.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.ui.theme.AppTheme

@Composable
fun SettingsDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.width(200.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.settings), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.there_is_nothing),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = { onDismissRequest() }) {
                    Text(stringResource(R.string.save))
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