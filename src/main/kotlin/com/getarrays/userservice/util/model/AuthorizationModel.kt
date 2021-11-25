package com.getarrays.userservice.util.model

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT

data class AuthorizationModel (
    val token: String,
    val algorithm: Algorithm,
    val verifier: JWTVerifier,
    val decodeJWT: DecodedJWT,
    val username: String
)