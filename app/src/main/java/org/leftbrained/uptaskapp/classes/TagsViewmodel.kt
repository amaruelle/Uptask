package org.leftbrained.uptaskapp.classes

import org.jetbrains.exposed.sql.transactions.transaction

class TagsViewmodel {
    fun getTags(taskId: Int): List<Tag> {
        transaction {
            return@transaction Tag.find { UptaskDb.TaskTags.taskId eq taskId }.toList<Tag>()
        }
        return emptyList()
    }

    fun addTag(tag: Tag) {
        transaction {
            Tag.new {
                taskId = tag.taskId
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