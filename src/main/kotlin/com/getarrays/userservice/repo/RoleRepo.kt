package com.getarrays.userservice.repo

import com.getarrays.userservice.domain.Role
import com.getarrays.userservice.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepo : JpaRepository<Role, Long> {
    fun findByName(name: String): Role
}