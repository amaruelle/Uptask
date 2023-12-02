package org.leftbrained.uptaskapp.classes

import org.jetbrains.exposed.sql.transactions.transaction

class UsersViewmodel {
    fun getUser(login: String): User? {
        transaction {
            val usr = User.find { UptaskDb.Users.login eq login }.firstOrNull()
            if (usr != null) {
                return@transaction usr
            } else {
                return@transaction null
            }
        }
        return null
    }

    fun addUser(user: User) {
        transaction {
            User.new {
                login = user.login
                password = user.password
            }
        }
    }

    fun deleteUser(login: String) {
        transaction {
            User.find { UptaskDb.Users.login eq login }.firstOrNull()?.delete()
        }
    }
}