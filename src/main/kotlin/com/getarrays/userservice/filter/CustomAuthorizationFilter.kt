package com.getarrays.userservice.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import java.util.Arrays.stream


class CustomAuthorizationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.servletPath.equals("/api/login") || request.servletPath.equals("/api/token/refresh")) {
            filterChain.doFilter(request, response)
        } else {
            val authorizaionHeader: String = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (authorizaionHeader != null && authorizaionHeader.startsWith("Bearer ")) {
                try {
                    val token: String = authorizaionHeader.substring("Bearer ".length)
                    val algorithm: Algorithm = Algorithm.HMAC256("secret".toByteArray())
                    val verifier: JWTVerifier = JWT.require(algorithm).build()
                    val decodeJWT: DecodedJWT = verifier.verify(token)
                    val username: String = decodeJWT.subject
                    val roles: Array<String> = decodeJWT.getClaim("roles").asArray(String::class.java)
                    val authorities: MutableCollection<SimpleGrantedAuthority> = mutableListOf<SimpleGrantedAuthority>()
                    stream(roles).forEach{role -> authorities.add(SimpleGrantedAuthority(role))}
                    val authenticationToken: UsernamePasswordAuthenticationToken
                        = UsernamePasswordAuthenticationToken(username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                    filterChain.doFilter(request, response)
                } catch (e: Exception) {
                    e.printStackTrace()
                    response.setHeader("error", e.message)
                    response.status = HttpServletResponse.SC_FORBIDDEN
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN)
                    val error: HashMap<String, String> = hashMapOf()
                    error.put("error_message", e.message!!)
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    ObjectMapper().writeValue(response?.outputStream, error)
                }
            } else {
                filterChain.doFilter(request, response)
            }
        }
    }
}