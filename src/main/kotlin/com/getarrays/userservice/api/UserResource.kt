package com.getarrays.userservice.api

import com.getarrays.userservice.domain.Role
import com.getarrays.userservice.domain.User
import com.getarrays.userservice.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api")
class UserResource {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok().body(userService.getUsers())
    }

    @PostMapping("/user/save")
    fun saveUser(@RequestBody user: User): ResponseEntity<User> {
        val uri: URI = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString())
        return ResponseEntity.created(uri).body(userService.saveUser(user))
    }

    @PostMapping("/role/save")
    fun saveRole(@RequestBody role: Role): ResponseEntity<Role> {
        val uri: URI = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString())
        return ResponseEntity.created(uri).body(userService.saveRole(role))
    }

    @PostMapping("/role/addToUser")
    fun addRoleToUser(@RequestBody form: RoleToUserForm): ResponseEntity<Any> {
        userService.addRoleToUser(form.username, form.roleName)
        return ResponseEntity.ok().build()
    }
}

data class RoleToUserForm (
    val username: String,
    val roleName: String
)