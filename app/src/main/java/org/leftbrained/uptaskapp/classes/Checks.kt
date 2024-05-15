package org.leftbrained.uptaskapp.classes

import android.app.Activity
import android.content.Context
import android.widget.Toast
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.db.Tag
import org.leftbrained.uptaskapp.db.UptaskDb

object Checks {
    fun emptyCheck(
        name: String,
        context: Context
    ): Boolean {
        if (name == "") {
            Toast.makeText(
                context,
                context.getString(R.string.empty_not_allowed),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun priorityCheck(priority: Int, context: Context): Boolean {
        if (!priority.toString().matches(Regex("[0-5]"))) {
            Toast.makeText(
                context,
                context.getString(R.string.priority_between),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun tagCheck(tag: String, context: Context): Boolean {
        if (tag == "") {
            Toast.makeText(
                context,
                context.getString(R.string.tag_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun tagExistsCheck(tag: String, context: Context): Boolean {
        if (transaction {
                Tag.find {
                    UptaskDb.TaskTags.tag eq tag
                }.toList().isNotEmpty()
            }) {
            Toast.makeText(
                context,
                context.getString(R.string.tag_already_exists),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun tagAddedCheck(tags: MutableList<String>, tag: String, context: Context): Boolean {
        if (tags.contains(tag)) {
            Toast.makeText(
                context,
                context.getString(R.string.tag_already_added),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun checkAuth(
        login: String,
        password: String,
        activity: Activity,
        context: Context
    ): Boolean {
        if (login == "" || password == "") {
            Toast.makeText(
                activity,
                context.getString(R.string.please_fill_all_fields),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
}