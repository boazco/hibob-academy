package com.hibob.academy.filters

import com.hibob.academy.service.SessionService.Companion.SECRET_KEY
import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Department
import com.hibob.feedback.dao.Role
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.springframework.stereotype.Component
import io.jsonwebtoken.Jwts
import java.util.UUID


@Component
@Provider
class AuthenticationFilter : ContainerRequestFilter {
    companion object {
        const val cookieName = "Jwt"
        const val ignoreThisUrl = "api/login"
        const val activeUserPropertyName = "activeUser"
    }

    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == ignoreThisUrl) return

        val jwtCookie = requestContext.cookies[cookieName]?.value
        val claims = verifyAndExtractClaims(jwtCookie, requestContext)
        claims?.let {
            val activeUser =
                ActiveUser(
                    UUID.fromString(it["employeeId"].toString()),
                    UUID.fromString(it["companyId"].toString()),
                    Role.valueOf(it["role"]!!.uppercase()),
                    Department.valueOf(it["department"]!!.uppercase())
                )
            requestContext.setProperty("activeUser", activeUser)
        }
    }

    fun verifyAndExtractClaims(cookie: String?, requestContext: ContainerRequestContext): Map<String, String>? {
        return cookie?.let {
            try {
                val claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(it).body

                mapOf(
                    "employeeId" to claims["employeeId"].toString(), "companyId" to claims["companyId"].toString(),
                    "role" to claims["role"].toString(), "department" to claims["department"].toString(),
                )
            } catch (e: Exception) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
                null
            }
        }
    }

}