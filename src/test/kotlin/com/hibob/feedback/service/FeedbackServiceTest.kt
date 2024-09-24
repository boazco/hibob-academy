package com.hibob.feedback.service

import com.hibob.feedback.dao.FeedbackDao



class FeedbackServiceTest{

    private val feedbackDaoMock = Mock<FeedbackDao>()
    private val service = FeedbackService(feedbackDaoMock)

}