package org.leftbrained.uptaskapp.classes

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Task (
    var name: String = "Test task",
    var description: String = "Lorem ipsum dolor sit amet",
    var dueDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    var tags: MutableList<Tag> = mutableListOf()
)
