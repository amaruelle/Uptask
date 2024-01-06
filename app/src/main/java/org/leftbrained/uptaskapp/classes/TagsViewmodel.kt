package org.leftbrained.uptaskapp.classes

import androidx.lifecycle.ViewModel
import org.jetbrains.exposed.sql.transactions.transaction

class TagsViewmodel : ViewModel() {
    fun getTags(taskId: Int): List<Tag> = transaction {
        Tag.find { UptaskDb.TaskTags.taskId eq taskId }.toList()
    }

    fun addTag(tag: Tag) {
        transaction {
            Tag.new {
                this.taskId = tag.taskId
                this.tag = tag.tag
            }
        }
    }

    fun deleteTag(tagId: Int) {
        transaction {
            Tag.find {
                UptaskDb.TaskTags.id eq tagId
            }.firstOrNull()?.delete()
        }
    }
}