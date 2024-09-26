package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import jakarta.ws.rs.BadRequestException
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class ResponseServiceTest{
    private val responseDaoMock = mock<ResponseDao>()
    private val feedbackDaoMock = mock<FeedbackDao>()
    private val service = ResponseService(responseDaoMock, feedbackDaoMock)
    private val activeUser = ActiveUser(UUID.randomUUID(), UUID.randomUUID(), Role.ADMIN, Department.HR)
    private val feedbackId = UUID.randomUUID()
    private val responseInput = ResponseInput(feedbackId,"Feedback for testing", activeUser.employeeId)


    @Test
    fun `when no feedback is found, it throws exception`() {
        whenever(feedbackDaoMock.getFeedback(responseInput.feedbackId, activeUser)).thenThrow(BadRequestException::class.java)
        org.junit.jupiter.api.assertThrows<BadRequestException> { service.createResponse(responseInput, activeUser) }
    }

//    @Test
//    fun `change status is being called on a valid response`(){
//        service.createResponse(responseInput, activeUser)
//        verify(feedbackDaoMock).changeStatus(responseInput.feedbackId, Status.REVIEWED, activeUser)
//    }

}