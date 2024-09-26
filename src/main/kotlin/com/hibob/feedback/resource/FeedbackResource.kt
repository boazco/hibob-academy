package com.hibob.feedback.resource

import com.hibob.feedback.service.FeedbackService
import com.hibob.feedback.dao.*
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import java.util.*
import jakarta.ws.rs.core.MediaType


@Component
@Path("/api/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(private val feedbackService: FeedbackService) {

    @POST
    @Path("/v1/create")
    fun createFeedback(feedbackInput: FeedbackInput, @Context requestContext: ContainerRequestContext): Response {
        val activeUser = UserAuthentication.getActiveUserOrThrow(requestContext)
        val feedbackId = feedbackService.createFeedback(feedbackInput, activeUser)
        return Response.ok(feedbackId).build()
    }

    @GET
    @Path("/v1/feedbackId/{feedbackId}")
    fun getFeedback(
        @PathParam("feedbackId") feedbackId: UUID,
        @Context requestContext: ContainerRequestContext
    ): Response {
        val activeUser = UserAuthentication.getActiveUserOrThrow(requestContext)


        UserAuthentication.throwIfNotAuthorized(activeUser)
        val feedback = feedbackService.getFeedback(feedbackId, activeUser)
        return Response.ok(feedback).build() //TO DO CHANGE IT to return the output from the service.
    }

}
