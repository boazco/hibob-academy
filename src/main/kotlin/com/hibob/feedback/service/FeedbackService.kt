package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Feedback
import com.hibob.feedback.dao.FeedbackInput
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

}