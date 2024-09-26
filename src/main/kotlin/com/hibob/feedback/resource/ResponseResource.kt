package com.hibob.feedback.resource


import com.hibob.feedback.dao.ResponseInput
import com.hibob.feedback.service.ResponseService
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component

@Component
@Path("/api/response")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ResponseResource(private val responseService: ResponseService) {

    @POST
    @Path("/v1")
    fun createResponse(response: ResponseInput, @Context requestContext: ContainerRequestContext): Response {
        val activeUser = UserAuthentication.getActiveUserOrThrow(requestContext)
        UserAuthentication.throwIfNotAuthorized(activeUser)
        val responseId = responseService.createResponse(response, activeUser)
        return Response.ok(responseId).build()
    }

}