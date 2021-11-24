package com.getarrays.userservice.repo

import com.getarrays.userservice.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo : JpaRepository<User, Long> {
    fun findByUsername(username: String?): User
}