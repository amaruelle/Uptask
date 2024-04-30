package org.leftbrained.uptaskapp.viewmodel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.R
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User

class UserViewModel : ViewModel() {
    fun authUser(login: String, password: String, activity: Activity, context: Context): User? {
        val user = transaction { User.find { UptaskDb.Users.login eq login }.firstOrNull() }
        if (user == null) {
            Toast.makeText(
                activity,
                context.getString(R.string.user_with_this_login),
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        if (user.password != password) {
            Toast.makeText(
                activity,
                context.getString(R.string.incorrect_password),
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        return user
    }

    fun setCurrentUser(
        sharedPref: SharedPreferences,
        user: User
    ) {
        transaction {
            with(sharedPref.edit()) {
                putString("user", user.id.value.toString())
                apply()
            }
        }
    }

    fun newUser(login: String, password: String): User = transaction {
        User.new { this.login = login; this.password = password }
    }
}