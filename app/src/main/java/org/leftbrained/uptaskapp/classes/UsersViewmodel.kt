package org.leftbrained.uptaskapp.classes

import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.db.UptaskDb
import org.leftbrained.uptaskapp.db.User

class UsersViewmodel {
    fun getUser(login: String): User? = transaction {
        User.find { UptaskDb.Users.login eq login }.firstOrNull()
    }

    fun addUser(user: User) = transaction {
        User.new {
            login = user.login
            password = user.password
        }
    }

    fun deleteUser(login: String) = transaction {
        User.find { UptaskDb.Users.login eq login }.firstOrNull()?.delete()
    }
}