package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import jakarta.ws.rs.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Component
class FeedbackService(private val feedbackDao: FeedbackDao, private val employeesDao: EmployeesDao) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: ActiveUser): UUID {
        return if (feedbackInput.isAnonymous) {
            feedbackDao.createFeedback(feedbackInput, activeUser.copy(employeeId = null))
        } else {
            feedbackDao.createFeedback(feedbackInput, activeUser)
        }
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        val feedback = feedbackDao.getFeedback(feedbackId, activeUser)
        if (employeesDao.getDepartmentById(activeUser.employeeId) != "HR"
            && employeesDao.getRoleById(activeUser.employeeId) != "Admin"
            && /*the following condition or feedback.employeeId != activeUser.employeeId*/employeesDao.getEmployeeIdByFeedbackId(
                feedbackId
            ) != activeUser.employeeId
        ) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized Access- Trying to fetch feedback which is not yours, while youre not HR or admin"
            )
        }
        if (feedback == null) {
            throw BadRequestException("Feedback not found")
        }
        return feedback //Do we want to throw exception when no feedback is found? or only when no access
    }

}