package com.getarrays.userservice

import com.getarrays.userservice.domain.Role
import com.getarrays.userservice.domain.User
import com.getarrays.userservice.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class UserserviceApplication(val userService: UserService) : CommandLineRunner {
	override fun run(vararg args: String?) {
		userService.saveRole(Role(null, "ROLE_USER"))
		userService.saveRole(Role(null, "ROLE_MANAGER"))
		userService.saveRole(Role(null, "ROLE_ADMIN"))
		userService.saveRole(Role(null, "ROLE_SUPER_ADMIN"))

		userService.saveUser(User(null, "山田太郎", "山田", "1234",  mutableListOf<Role>()))
		userService.saveUser(User(null, "武田信玄", "武田", "1234",  mutableListOf<Role>()))
		userService.saveUser(User(null, "北条氏康", "北条", "1234",  mutableListOf<Role>()))
		userService.saveUser(User(null, "今川義元", "今川", "1234",  mutableListOf<Role>()))

		userService.addRoleToUser("山田", "ROLE_USER")
		userService.addRoleToUser("山田", "ROLE_MANAGER")
		userService.addRoleToUser("武田", "ROLE_MANAGER")
		userService.addRoleToUser("北条", "ROLE_ADMIN")
		userService.addRoleToUser("今川", "ROLE_SUPER_ADMIN")
		userService.addRoleToUser("今川", "ROLE_ADMIN")
		userService.addRoleToUser("今川", "ROLE_USER")

	}
}

fun main(args: Array<String>) {
	runApplication<UserserviceApplication>(*args)
}

@Bean
fun passwordEncoder(): PasswordEncoder? {
	return BCryptPasswordEncoder()
}