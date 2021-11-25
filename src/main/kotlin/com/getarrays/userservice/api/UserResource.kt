package com.getarrays.userservice.api

import com.auth0.jwt.JWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.getarrays.userservice.domain.Role
import com.getarrays.userservice.domain.User
import com.getarrays.userservice.service.UserService
import com.getarrays.userservice.util.AuthUtil
import com.getarrays.userservice.util.model.AuthorizationModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.sql.Date
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

    @PostMapping("/token/refresh")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse) {
        val authorizaionHeader: String = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizaionHeader != null && authorizaionHeader.startsWith("Bearer ")) {
            try {
                val authorizationModel: AuthorizationModel = AuthUtil.createAuthorization(authorizaionHeader)
                val user: User = userService.getUser(authorizationModel.username)

                val access_token: String = JWT.create()
                    .withSubject(user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(request?.requestURL.toString())
                    .withClaim("roles", user.role.stream().map(Role::name).collect(Collectors.toList()))
                    .sign(authorizationModel.algorithm)

                response?.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response?.outputStream
                    , AuthUtil.setTokens(access_token, authorizationModel.token))
            } catch (e: Exception) {
                AuthUtil.setAuthorizationError(response, e)
            }
        } else {
            throw RuntimeException("Refresh token is misssing")
        }
    }
}

data class RoleToUserForm (
    val username: String,
    val roleName: String
)