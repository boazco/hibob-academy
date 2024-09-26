package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class ResponseService(private val responseDao: ResponseDao, private val feedbackDao: FeedbackDao) {

    fun createResponse(response: ResponseInput, activeUser: ActiveUser): UUID {
        feedbackDao.getFeedback(response.feedbackId, activeUser) //throws if feedback not found
        val responseId = responseDao.createResponse(response, activeUser)
        feedbackDao.changeStatus(response.feedbackId, Status.REVIEWED, activeUser)
        return responseId
    }
}