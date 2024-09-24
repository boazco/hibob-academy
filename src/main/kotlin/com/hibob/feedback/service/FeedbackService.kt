package com.hibob.feedback.service

import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Feedback
import com.hibob.feedback.dao.FeedbackInput
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotAuthorizedException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import java.time.LocalDate
import java.util.*

@Component
class FeedbackService(/* Will need to get the dao*/) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: ActiveUser): UUID {
        return if (feedbackInput.isAnonymous) {
            FeedbackDao.createFeedback(feedbackInput, activeUser.copy(employeeId = null))
        } else {
            feedbackDao.createFeedback(feedbackInput, activeUser)
        }
        //Will need to add department into the feedback in Phase2 (from the DB, here or in the filter)
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        val feedback: Feedback = FeedbackDao.getFeedback(feedbackId, activeUser)
        if (EmployeesDao.getDepartmentById(activeUser.employeeId) != "HR"
            && EmployeesDao.getRoleById(activeUser.employeeId) != "Admin"
            && EmployeeDao.getEmployeeIdByFeedbackId(feedbackId) != activeUser.employeeId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED
                , "Unauthorized Access- Trying to fetch feedback which is not yours, while youre not HR or admin")
        }
            val feedback = EmployeeDao.getFeedback(feedbackId, activeUser)
        if(feedback == null) {
            throw BadRequestException(HttpStatus.BAD_REQUEST, "Feedback not found")
        }
        return feedback //Do we want to throw exception when no feedback is found? or only when no access
    }

}