package com.getarrays.userservice.service

import com.getarrays.userservice.domain.Role
import com.getarrays.userservice.domain.User
import com.getarrays.userservice.passwordEncoder
import com.getarrays.userservice.repo.RoleRepo
import com.getarrays.userservice.repo.UserRepo
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Slf4j
class UserServiceImpl(val log: Logger) : UserService, UserDetailsService {

    @Autowired
    lateinit var userRepo: UserRepo
    @Autowired
    lateinit var roleRepo: RoleRepo

    override fun saveUser(user: User): User {
        log.info("Saving new user {} to the database", user.name)
        user.password = passwordEncoder()?.encode(user.password)!!
        return userRepo.save(user)
    }

    override fun saveRole(role: Role): Role {
        log.info("Saving new role to the database", role.name)
        return roleRepo.save(role)
    }

    override fun addRoleToUser(username: String, roleName: String) {
        log.info("adding role {} to user {}", roleName, username)
        val user: User = userRepo.findByUsername(username)
        val role: Role = roleRepo.findByName(roleName)
        user.role.add(role)
    }

    override fun getUser(username: String): User {
        log.info("Fetching User {}", username)
        return userRepo.findByUsername(username)
    }

    override fun getUsers(): List<User> {
        log.info("Fetching Users")
        return userRepo.findAll()
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        val user: User = userRepo.findByUsername(username)
        if (user == null) {
            log.error("User not found")
            throw UsernameNotFoundException("User not found")
        } else {
            log.info("User found in database: {}", username)
        }
        val authorities: MutableCollection<SimpleGrantedAuthority> = mutableListOf<SimpleGrantedAuthority>()
        user.role.forEach{
            role -> authorities.add(SimpleGrantedAuthority(role.name))
        }
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }
}