package com.hibob.feedback.service


import com.hibob.feedback.dao.*
import jakarta.ws.rs.BadRequestException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import java.time.LocalDate
import java.util.*

class FeedbackServiceTest {

    private val feedbackDaoMock = mock<FeedbackDao>()
    private val employeeDaoMock = mock<EmployeesDao>()
    private val service = FeedbackService(feedbackDaoMock, employeeDaoMock)
    private val activeUser = ActiveUser(UUID.randomUUID(), UUID.randomUUID())
    private val feedbackInput = FeedbackInput("Feedback for testing", true)
    private val feedbackId = UUID.randomUUID()

    @Test
    fun `create anonymous should not use employee id`() {
        whenever(
            feedbackDaoMock.createFeedback(
                feedbackInput,
                activeUser.copy(employeeId = null)
            )
        ).thenReturn(UUID.randomUUID())
        assertEquals(true, service.createFeedback(feedbackInput, activeUser) is UUID)
        verify(feedbackDaoMock).createFeedback(feedbackInput, activeUser.copy(employeeId = null))
    }

    @Test
    fun `create not anonymous should use employee id`() {
        whenever(
            feedbackDaoMock.createFeedback(
                feedbackInput.copy(isAnonymous = false),
                activeUser
            )
        ).thenReturn(UUID.randomUUID())
        assertEquals(true, service.createFeedback(feedbackInput.copy(isAnonymous = false), activeUser) is UUID)
        verify(feedbackDaoMock).createFeedback(feedbackInput.copy(isAnonymous = false), activeUser)
    }

    @Test
    fun `get feedbacks when not HR or Admin or feedback author should throw`() {
        whenever(feedbackDaoMock.getFeedback(feedbackId, activeUser)).thenReturn(null)
        whenever(employeeDaoMock.getRoleById(activeUser.employeeId!!, activeUser.companyId)).thenReturn("IT")
        whenever(employeeDaoMock.getDepartmentById(activeUser.employeeId!!, activeUser.companyId)).thenReturn("Chef")
        assertEquals(
            "401 UNAUTHORIZED \"Unauthorized Access- Trying to fetch feedback which is not yours, while youre not HR or admin\"",
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                service.getFeedback(
                    feedbackId,
                    activeUser
                )
            }.message
        )
    }

    @Test
    fun `when no feedback is found should throw exception`() {
        whenever(feedbackDaoMock.getFeedback(feedbackId, activeUser)).thenReturn(null)
        whenever(employeeDaoMock.getRoleById(activeUser.employeeId!!, activeUser.companyId)).thenReturn("Admin")
        whenever(employeeDaoMock.getDepartmentById(activeUser.employeeId!!, activeUser.companyId)).thenReturn("HR")
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
        whenever(employeeDaoMock.getRoleById(activeUser.employeeId!!, activeUser.companyId)).thenReturn("Admin")
        whenever(employeeDaoMock.getDepartmentById(activeUser.employeeId!!, activeUser.companyId)).thenReturn("HR")
        assertEquals(returningFeedback, service.getFeedback(feedbackId, activeUser))
    }

}