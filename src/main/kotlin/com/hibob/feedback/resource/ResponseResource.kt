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
class ResponseResource(private val responseService: ResponseService, private val feedbackResource: FeedbackResource) {

    @POST
    @Path("/v1/create")
    fun createResponse(response: ResponseInput, @Context requestContext: ContainerRequestContext): Response {
        val activeUser = feedbackResource.getActiveUserOrThrow(requestContext)
        feedbackResource.throwIfNotAuthorized(activeUser)
        val responseId = responseService.createResponse(response, activeUser)
        return Response.ok(responseId).build()
    }


}