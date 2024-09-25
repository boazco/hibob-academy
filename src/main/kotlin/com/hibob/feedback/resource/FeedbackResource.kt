package com.hibob.feedback.resource

import com.hibob.feedback.service.FeedbackService
import com.hibob.feedback.dao.*
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import java.util.*
import jakarta.ws.rs.core.MediaType


@Component
@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(private val feedbackService: FeedbackService) {

    @POST
    @Path("/v1/create")
    fun createFeedback(feedbackInput: FeedbackInput): Response {
        val activeUser = Employee(UUID.randomUUID(), UUID.randomUUID(), Role.EMPLOYEE, Department.HR) //TO DO: change it to take proprties from the header
        val feedbackId = feedbackService.createFeedback(feedbackInput, activeUser)
        return Response.ok(feedbackId).build()
    }

    @GET
    @Path("/v1/feedbackId/{feedbackId}")
    fun getFeedback(@PathParam("feedbackId") feedbackId: UUID): Response {
        val activeUser = Employee(UUID.randomUUID(), UUID.randomUUID(), Role.EMPLOYEE, Department.HR) //TO DO: change it to take proprties from the header
        val feedback = feedbackService.getFeedback(feedbackId, activeUser)
        return Response.ok(feedback).build()
    }
}
