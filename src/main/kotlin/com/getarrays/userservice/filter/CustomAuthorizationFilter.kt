package com.getarrays.userservice.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.getarrays.userservice.util.AuthUtil
import com.getarrays.userservice.util.model.AuthorizationModel
import org.springframework.beans.factory.annotation.Autowired
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
                    val authorizationModel: AuthorizationModel = AuthUtil.createAuthorization(authorizaionHeader)
                    val roles: Array<String> = authorizationModel.decodeJWT.getClaim("roles").asArray(String::class.java)
                    val authorities: MutableCollection<SimpleGrantedAuthority> = mutableListOf<SimpleGrantedAuthority>()
                    stream(roles).forEach{role -> authorities.add(SimpleGrantedAuthority(role))}
                    val authenticationToken: UsernamePasswordAuthenticationToken
                        = UsernamePasswordAuthenticationToken(authorizationModel.username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                    filterChain.doFilter(request, response)
                } catch (e: Exception) {
                    AuthUtil.setAuthorizationError(response, e)
                }
            } else {
                filterChain.doFilter(request, response)
            }
        }
    }
}