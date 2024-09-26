package com.hibob.feedback.resource

import com.hibob.academy.filters.AuthenticationFilter.Companion.activeUserPropertyName
import com.hibob.feedback.service.FeedbackService
import com.hibob.feedback.dao.*
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import jakarta.ws.rs.core.MediaType
import java.util.*
import org.springframework.web.bind.annotation.RequestBody


@Component
@Path("/api/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(private val feedbackService: FeedbackService) {

    @POST
    @Path("/v1/create")
    fun createFeedback(feedbackInput: FeedbackInput, @Context requestContext: ContainerRequestContext): Response {
        val activeUser = getActiveUserOrThrow(requestContext)
        val feedbackId = feedbackService.createFeedback(feedbackInput, activeUser)
        return Response.ok(feedbackId).build()
    }

    private fun getActiveUserOrThrow(requestContext: ContainerRequestContext): ActiveUser {
        return requestContext.getProperty(activeUserPropertyName) as? ActiveUser
            ?: throw BadRequestException("user is not an active user")
    }

    @GET
    @Path("/v1/feedbackId/{feedbackId}")
    fun getFeedback(
        @PathParam("feedbackId") feedbackId: UUID,
        @Context requestContext: ContainerRequestContext
    ): Response {
        val activeUser = getActiveUserOrThrow(requestContext)


        throwIfNotAuthorized(activeUser)
        val feedback = feedbackService.getFeedback(feedbackId, activeUser)
        return Response.ok(feedback).build() //TO DO CHANGE IT to return the output from the service.
    }

    private fun throwIfNotAuthorized(activeUser: ActiveUser) {
        if (!(activeUser.department == Department.HR || activeUser.role == Role.ADMIN)) {
            throw NotAuthorizedException(
                "Unauthorized Access- Trying to fetch feedback which is not yours, while youre not HR or admin"
            )
        }
    }

    @GET
    @Path("/v1/getFeedbackStatus/{feedbackId}")
    fun getStatus(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: UUID): Response {
        val activeUser = getActiveUserOrThrow(requestContext)
        return Response.ok(feedbackService.getStatus(feedbackId, activeUser)).build()
    }

    @POST
    @Path("/v1/filterFeedbacks")
    fun filterFeedbacks(@RequestBody filter: Filter, @Context requestContext: ContainerRequestContext): Response {
        val activeUser = getActiveUserOrThrow(requestContext)
        throwIfNotAuthorized(activeUser)
        return Response.ok(feedbackService.filterFeedbacks(filter, activeUser)).build()
    }


}
