package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class ResponseService(private val responseDao: ResponseDao, private val feedbackDao: FeedbackDao) {

    fun createResponse(reaponse: ResponseInput, activeUser: ActiveUser): UUID {
        val responseId = responseDao.createResponse(reaponse, activeUser)

        return responseId
    }
}