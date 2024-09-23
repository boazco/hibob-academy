package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import org.springframework.http.HttpStatus
import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Feedback
import com.hibob.feedback.dao.FeedbackInput
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.LocalDate
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Component
class FeedbackService(private val feedbackDao: FeedbackDao, private val employeesDao: EmployeesDao) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: ActiveUser): UUID {
        return feedbackDao.createFeedback(feedbackInput, activeUser)
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        val feedback = feedbackDao.getFeedback(feedbackId, activeUser)
        if (isNotAuthorized(employeesDao.getEmployeeByActiveUser(activeUser), feedback)) {
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