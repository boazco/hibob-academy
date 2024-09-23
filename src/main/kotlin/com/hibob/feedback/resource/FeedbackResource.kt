package com.hibob.feedback.resource

import com.hibob.academy.service.SessionService.Companion.SECRET_KEY
import com.hibob.feedback.dao.*
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import java.sql.Date
import java.time.LocalDate
import java.util.*
import jakarta.ws.rs.core.MediaType


@Component
@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(/*feedback Serrvice*/) {
    companion object {
        val cookieName = "Jwt"
    }

    private fun buildActiveUser(requestContext: ContainerRequestContext): ActiveUser{
        val cookie = requestContext.cookies[cookieName]?.value
        val claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(cookie).body
        val companyId = claims["companyId"] as UUID
        return ActiveUser(claims["employeeId"] as UUID, companyId)
    }

    @POST
    @Path("/v1/create")
    fun createFeedback(@RequestBody feedbackInput: FeedbackInput, requestContext: ContainerRequestContext): Response {
        val activeUser = buildActiveUser(requestContext)
        //send the service active user and feedbackInput.
        return Response.ok().build() //TO DO CHANGE IT to return the output from the service.
    }

    @GET
    @Path("/v1/feedbackId/{feedbackId}")
    fun getFeedback(@PathParam("feedbackId") feedbackId: UUID, requestContext: ContainerRequestContext): Response {
        val activeUser = buildActiveUser(requestContext)
        //send the service active user and feedbackId.
        return Response.ok().build() //TO DO CHANGE IT to return the output from the service.
    }

    @GET
    @Path("/v1/feedbackId/{feedbackId}/response/{responseId}")
    fun getFeedbackResponse(@PathParam("feedbackId") feedbackId: UUID, @PathParam("responseId") responseId: UUID, requestContext: ContainerRequestContext): Response {
        val activeUser = buildActiveUser(requestContext)
        //send the service active user, feedbackId and responseId
        return Response.ok().build() //TO DO CHANGE IT to return the output from the service.
    }


}
