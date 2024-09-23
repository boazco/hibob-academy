package com.hibob.feedback.dao

import org.junit.jupiter.api.Test
import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Date
import java.time.LocalDate
import java.util.*

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val feedbackDao = FeedbackDao(sql)
    private val feedbackTable = FeedbackTable.instance
    private val companyId = UUID.randomUUID()

    private val feedback =
        Feedback(UUID.randomUUID(), Date.valueOf(LocalDate.now()), companyId, "Feedback for testing!")
    private val feedback1 =
        Feedback(UUID.randomUUID(), Date.valueOf(LocalDate.now()), companyId, "Another feedback for testing!")
    private val feedback2 = Feedback(UUID.randomUUID(), Date.valueOf(LocalDate.now()), companyId, "Another one")
    private val activeUser = ActiveUser(UUID.randomUUID(), companyId)

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(feedbackTable).where(feedbackTable.companyId.eq(companyId)).execute()
    }

    @Test
    fun `create should create a feedback and return his id`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUser)
        assertEquals(feedbackId, feedback.id)
    }

    @Test
    fun `same employee can create multiple feedbacks`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUser)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUser)
        val feedbackId2 = feedbackDao.createFeedback(feedback2, activeUser)
        assertEquals(feedbackId, feedback.id)
        assertEquals(feedbackId1, feedback1.id)
        assertEquals(feedbackId2, feedback2.id)
    }

    @Test
    fun `create and the get should return feedback`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUser)
        assertEquals(feedback, feedbackDao.getFeedback(feedbackId, activeUser))
    }

    @Test
    fun `get where no feedback with that ID return null`() {
        assertEquals(null, feedbackDao.getFeedback(UUID.randomUUID(), activeUser))
    }
}