package com.getarrays.userservice.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.sql.Date
import java.util.*
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CustomAuthenticationFilter(private val authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val username = request?.getParameter("username")
        val passowrd = request?.getParameter("password")
        val authenticationToken: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(username, passowrd)
        return authManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val user: User = authResult?.principal as User
        val algorithm: Algorithm = Algorithm.HMAC256("secret".toByteArray())
        val access_token: String = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
            .withIssuer(request?.requestURL.toString())
            .withClaim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(algorithm)
        val refresh_token = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
            .withIssuer(request?.requestURL.toString())
            .sign(algorithm)
//        response?.setHeader("access_token", access_token)
//        response?.setHeader("refresh_token", refresh_token)
        val tokens: HashMap<String, String> = hashMapOf()
        tokens.put("access_token", access_token)
        tokens.put("refresh_token", refresh_token)
        response?.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response?.outputStream, tokens)
    }
}