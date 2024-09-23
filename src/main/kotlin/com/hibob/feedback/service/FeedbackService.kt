package com.hibob.feedback.service

import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Feedback
import com.hibob.feedback.dao.FeedbackInput
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.LocalDate
import java.util.*

@Component
class FeedbackService(/* Will need to get the dao*/) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: ActiveUser): UUID {
        val feedbackId = UUID.randomUUID()
        val feedback =
            Feedback(feedbackId, Date.valueOf(LocalDate.now()), activeUser.companyId, feedbackInput.feedbackMessage)
        //TO DO calling the Dao create function with the feedback with employeeId/null depend on the feedbackInput.isAnonymous
        //Will need to add department into the feedback in Phase2 (from the DB, here or in the filter)
        //In dao the "status" property will always be the same in creation
        return feedbackId
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        val feedback = 0 //Will be return feedbackDao.getFeedback(args)
        //TO DO: add fetching of active user department and role. if role != admin && department != HR && employeeId != feedBack employeeId
        // Throw exception 401 Unauthorized
        return null //Do we want to throw exception when no feedback is found? or only when no access
    }

}