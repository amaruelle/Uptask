package org.leftbrained.uptaskapp.classes

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Task (
    var isCompleted: Boolean = false,
    var name: String = "Test task",
    var description: String = "Lorem ipsum dolor sit amet",
    var dueDate: String = "2023-01-01",
    var tags: MutableList<String> = mutableListOf("First tag", "Second tag"),
    var priority: Int = 0
)
