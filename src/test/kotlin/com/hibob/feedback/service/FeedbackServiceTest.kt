package com.hibob.feedback.service


import com.hibob.feedback.dao.*
import jakarta.ws.rs.BadRequestException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.sql.Date
import java.time.LocalDate
import java.util.*

class FeedbackServiceTest {

    private val feedbackDaoMock = mock<FeedbackDao>()
    private val employeeDaoMock = mock<EmployeesDao>()
    private val service = FeedbackService(feedbackDaoMock, employeeDaoMock)
    private val activeUser = Employee(UUID.randomUUID(), UUID.randomUUID(), Role.EMPLOYEE, Department.HR)
    private val feedbackInput = FeedbackInput("Feedback for testing", true)
    private val feedbackId = UUID.randomUUID()

    @Test
    fun `create should return feedback id`() {
        whenever(
            feedbackDaoMock.createFeedback(
                feedbackInput,
                activeUser
            )
        ).thenReturn(UUID.randomUUID())
        assertEquals(true, service.createFeedback(feedbackInput, activeUser) is UUID)
    }

    @Test
    fun `when no feedback is found should throw exception`() {
        whenever(
            feedbackDaoMock.getFeedback(
                feedbackId,
                activeUser
            )
        ).thenThrow(BadRequestException("Feedback not found"))
        assertEquals(
            "Feedback not found",
            org.junit.jupiter.api.assertThrows<BadRequestException> {
                service.getFeedback(
                    feedbackId,
                    activeUser
                )
            }.message
        )
    }

    @Test
    fun `returning feedback when authorized and feedback is found`() {
        val returningFeedback = Feedback(
            feedbackId,
            Date.valueOf(LocalDate.now()),
            UUID.randomUUID(),
            "for TEST"
        )
        whenever(feedbackDaoMock.getFeedback(feedbackId, activeUser)).thenReturn(returningFeedback)
        assertEquals(returningFeedback, service.getFeedback(feedbackId, activeUser))
    }

}