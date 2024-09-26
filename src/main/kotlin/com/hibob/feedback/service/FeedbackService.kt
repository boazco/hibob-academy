package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Feedback
import com.hibob.feedback.dao.FeedbackInput
import jakarta.ws.rs.BadRequestException
import org.springframework.stereotype.Component
import java.util.*

@Component
class FeedbackService(private val feedbackDao: FeedbackDao, private val employeesDao: EmployeesDao) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: ActiveUser): UUID {
        return feedbackDao.createFeedback(feedbackInput, activeUser)
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        val feedback = feedbackDao.getFeedback(feedbackId, activeUser)
        return feedback
    }

    fun getStatus(feedbackId: UUID, activeUser: ActiveUser): Status{
        return feedbackDao.getStatus(feedbackId, activeUser)
    }

    fun filterFeedbacks(filter: Filter, activeUser: ActiveUser): List<Feedback>? {
        return feedbackDao.filterFeedbacks(filter, activeUser)
    }

    fun changeStatus(feedbackId: UUID, status: Status, activeUser: ActiveUser) {
        if (feedbackDao.changeStatus(feedbackId, status, activeUser) == 0)
            throw BadRequestException("No feedback found  with that id")
    }


}