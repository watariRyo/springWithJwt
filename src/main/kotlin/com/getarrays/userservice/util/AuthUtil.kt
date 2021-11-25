package com.getarrays.userservice.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.getarrays.userservice.util.model.AuthorizationModel
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletResponse

class AuthUtil {
    companion object {
        fun createAuthorization(authorizaionHeader: String): AuthorizationModel {
            val token: String = authorizaionHeader.substring("Bearer ".length)
            val algorithm: Algorithm = Algorithm.HMAC256("secret".toByteArray())
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            val decodeJWT: DecodedJWT = verifier.verify(token)
            val username: String = decodeJWT.subject
            return AuthorizationModel(token, algorithm, verifier, decodeJWT, username)
        }

        fun setTokens(access_token: String, refresh_token: String): HashMap<String, String> {
            val tokens: HashMap<String, String> = hashMapOf()
            tokens.put("access_token", access_token)
            tokens.put("refresh_token", refresh_token)
            return tokens
        }

        fun setAuthorizationError(response: HttpServletResponse, e: Exception) {
            e.printStackTrace()
            response.setHeader("error", e.message)
            response.status = HttpServletResponse.SC_FORBIDDEN
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN)
            val error: HashMap<String, String> = hashMapOf()
            error.put("error_message", e.message!!)
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            ObjectMapper().writeValue(response?.outputStream, error)
        }
    }
}