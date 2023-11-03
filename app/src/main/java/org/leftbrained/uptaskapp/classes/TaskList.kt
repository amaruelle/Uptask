package org.leftbrained.uptaskapp.classes

import kotlinx.serialization.Serializable

@Serializable
data class TaskList (
    var name: String = "Test",
    var emoji: String = "😇",
    var tasks: MutableList<Task> = mutableListOf(Task(), Task())
) {
    fun add(task: Task) {
        tasks.add(task)
    }
}