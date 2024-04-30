package org.leftbrained.uptaskapp.viewmodel

import androidx.lifecycle.ViewModel
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UserTask

class TagViewModel: ViewModel() {

    fun newTag(tagName: String, idTask: UserTask): Tag = Tag.new {
        tag = tagName
        this.taskId = idTask
    }

    fun removeTag(tagId: Int) = Tag.findById(tagId)?.delete()
}