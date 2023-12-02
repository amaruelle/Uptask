package org.leftbrained.uptaskapp.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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