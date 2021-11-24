package com.getarrays.userservice.service

import com.getarrays.userservice.domain.Role
import com.getarrays.userservice.domain.User

interface UserService {
    fun saveUser(user: User): User

    fun saveRole(role: Role): Role

    fun addRoleToUser(username: String, roleName: String)

    fun getUser(username: String): User

    fun getUsers(): List<User>


}