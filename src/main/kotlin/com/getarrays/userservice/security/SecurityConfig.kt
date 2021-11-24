package com.getarrays.userservice.security

import com.getarrays.userservice.filter.CustomAuthenticationFilter
import com.getarrays.userservice.passwordEncoder
import com.getarrays.userservice.repo.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(private val userRepo: UserRepo, private val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService)?.passwordEncoder(passwordEncoder())
    }

    override fun configure(http: HttpSecurity?) {
        val customAuthenticationFilter: CustomAuthenticationFilter = CustomAuthenticationFilter(authenticationManagerBean())
        customAuthenticationFilter.setFilterProcessesUrl("/api/login")

        http?.csrf()?.disable()
        http?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http?.authorizeRequests()?.antMatchers("/api/login/**")?.permitAll()
        http?.authorizeRequests()?.antMatchers(HttpMethod.GET, "/api/user/**")?.hasAuthority("ROLE_USER")
        http?.authorizeRequests()?.antMatchers(HttpMethod.POST, "/api/user/save/**")?.hasAuthority("ROLE_ADMIN")
        http?.authorizeRequests()?.anyRequest()?.authenticated()
        http?.addFilter(customAuthenticationFilter)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

}