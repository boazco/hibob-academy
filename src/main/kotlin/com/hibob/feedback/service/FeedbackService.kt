package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Component
class FeedbackService(private val feedbackDao: FeedbackDao, private val employeesDao: EmployeesDao) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: Employee): UUID {
        return feedbackDao.createFeedback(feedbackInput, activeUser)
    }

    fun getFeedback(feedbackId: UUID, activeUser: Employee): Feedback? {
        val feedback = feedbackDao.getFeedback(feedbackId, activeUser)
        if (isNotAuthorized(activeUser, feedback)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized Access- Trying to fetch feedback which is not yours, while youre not HR or admin"
            )
        }
        return feedback
    }

    private fun isNotAuthorized(employee: Employee, feedback: Feedback): Boolean {
        return !(employee.department == Department.HR || employee.role == Role.ADMIN || employee.employeeId == feedback.employeeId)
    }

}