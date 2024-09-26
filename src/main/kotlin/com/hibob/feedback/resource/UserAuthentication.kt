package com.hibob.feedback.resource

import com.hibob.academy.filters.AuthenticationFilter.Companion.activeUserPropertyName
import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Department
import com.hibob.feedback.dao.Role
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotAuthorizedException
import jakarta.ws.rs.container.ContainerRequestContext

object UserAuthentication {
    fun getActiveUserOrThrow(requestContext: ContainerRequestContext): ActiveUser {
        return requestContext.getProperty(activeUserPropertyName) as? ActiveUser
            ?: throw BadRequestException("user is not an active user")
    }

    fun throwIfNotAuthorized(activeUser: ActiveUser) {
        if (!(activeUser.department == Department.HR || activeUser.role == Role.ADMIN)) {
            throw NotAuthorizedException(
                "Unauthorized Access- Trying to fetch feedback which is not yours, while youre not HR or admin"
            )
        }
    }
}