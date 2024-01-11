package org.leftbrained.uptaskapp.classes

import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb

class TagsViewmodel {
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