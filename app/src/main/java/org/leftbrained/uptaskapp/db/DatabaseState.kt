package org.leftbrained.uptaskapp.db

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DatabaseStateViewmodel: ViewModel() {
    var databaseState by mutableIntStateOf(1)
}