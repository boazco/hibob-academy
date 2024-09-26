package com.hibob.feedback.dao

import org.junit.jupiter.api.Test
import com.hibob.academy.utils.BobDbTest
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
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
    private val feedbackTable = FeedbackDao.FeedbackTable.instance
    private val companyId = UUID.randomUUID()

    private val feedback =
        FeedbackInput("Feedback for testing!", false)
    private val feedback1 =
        FeedbackInput("Another feedback for testing!", false)
    private val feedback2 = FeedbackInput("Another one", false)
    private val activeUserHR = ActiveUser(UUID.randomUUID(), companyId, Role.ADMIN, Department.HR)
    private val activeUserIT = ActiveUser(UUID.randomUUID(), companyId, Role.EMPLOYEE, Department.IT)

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(feedbackTable).where(feedbackTable.companyId.eq(companyId)).execute()
    }

    @Test
    fun `create should create a feedback and return his id`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserHR)
        assertTrue(feedbackId is UUID)
    }

    @Test
    fun `same employee can create multiple feedbacks`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserHR)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUserHR)
        val feedbackId2 = feedbackDao.createFeedback(feedback2, activeUserHR)
        assertTrue(feedbackId is UUID)
        assertTrue(feedbackId1 is UUID)
        assertTrue(feedbackId2 is UUID)
    }

    @Test
    fun `create and the get should return feedback`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserHR)
        assertEquals(
            Feedback(
                id = feedbackId,
                Date.valueOf(LocalDate.now()),
                companyId,
                feedback.feedbackMessage,
                activeUserHR.employeeId
            ), feedbackDao.getFeedback(feedbackId, activeUserHR)
        )
    }

    @Test
    fun `get where no feedback with that ID return null`() {
        assertEquals(
            "Feedback not found",
            org.junit.jupiter.api.assertThrows<BadRequestException> {
                feedbackDao.getFeedback(
                    UUID.randomUUID(),
                    activeUserHR
                )
            }.message
        )
    }

    @Test
    fun `get status when youre the feedback author`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserHR)
        assertEquals(Status.UNREVIEWED, feedbackDao.getStatus(feedbackId, activeUserHR))
    }

    @Test
    fun `get status of anonymous feedback throws`() {
        val feedbackId = feedbackDao.createFeedback(feedback.copy(isAnonymous = true), activeUserHR)
        assertEquals(
            "feedback not found",
            org.junit.jupiter.api.assertThrows<NotFoundException> {
                feedbackDao.getStatus(
                    feedbackId,
                    activeUserHR
                )
            }.message
        )
    }

    @Test
    fun `update should update feedback status`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserHR)
        assertEquals(Status.UNREVIEWED, feedbackDao.getStatus(feedbackId, activeUserHR))
        feedbackDao.changeStatus(feedbackId, Status.REVIEWED, activeUserHR)
        assertEquals(Status.REVIEWED, feedbackDao.getStatus(feedbackId, activeUserHR))
        feedbackDao.changeStatus(feedbackId, Status.UNREVIEWED, activeUserHR)
        assertEquals(Status.UNREVIEWED, feedbackDao.getStatus(feedbackId, activeUserHR))
    }

    @Test
    fun `filter by department`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserIT)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUserIT)
        feedbackDao.createFeedback(feedback2, activeUserHR)
        val expectedFeedbacks: List<Feedback> = listOf(
            Feedback(
                feedbackId,
                Date.valueOf(LocalDate.now()),
                companyId,
                feedback.feedbackMessage,
                activeUserIT.employeeId
            ),
            Feedback(
                feedbackId1,
                Date.valueOf(LocalDate.now()),
                companyId,
                feedback1.feedbackMessage,
                activeUserIT.employeeId
            )
        )
        assertTrue(
            feedbackDao.filterFeedbacks(Filter(department = Department.IT), activeUserHR)
                ?.containsAll(expectedFeedbacks) ?: false
        )
    }

    @Test
    fun `filter by date of tomorrow should return empty list`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserIT)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUserIT)
        assertTrue(
            feedbackDao.filterFeedbacks(Filter(date = Date.valueOf(LocalDate.now().plusDays(1))), activeUserHR)
                ?.isEmpty() ?: false
        )
    }

    @Test
    fun `filter by date of yestarday should return both feedbacks`() {
        val feedbackId = feedbackDao.createFeedback(feedback, activeUserIT)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUserIT)
        val expectedFeedbacks: List<Feedback> = listOf(
            Feedback(
                feedbackId,
                Date.valueOf(LocalDate.now()),
                companyId,
                feedback.feedbackMessage,
                activeUserIT.employeeId
            ),
            Feedback(
                feedbackId1,
                Date.valueOf(LocalDate.now()),
                companyId,
                feedback1.feedbackMessage,
                activeUserIT.employeeId
            )
        )
        assertTrue(
            feedbackDao.filterFeedbacks(Filter(date = Date.valueOf(LocalDate.now().minusDays(1))), activeUserHR)
                ?.containsAll(expectedFeedbacks)
                ?: false
        )
    }

    @Test
    fun `filter by anonymous, not returning anonymous feedbacks`() {
        val feedbackId = feedbackDao.createFeedback(feedback.copy(isAnonymous = true), activeUserIT)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUserIT)
        assertEquals(
            listOf(
                Feedback(
                    feedbackId1,
                    Date.valueOf(LocalDate.now()),
                    companyId,
                    feedback1.feedbackMessage,
                    activeUserIT.employeeId
                )
            ), feedbackDao.filterFeedbacks(Filter(isAnonymous = false), activeUserHR)
        )
    }

    @Test
    fun `filter by anonymous, not returning unAnonymous feedbacks`() {
        val feedbackId = feedbackDao.createFeedback(feedback.copy(isAnonymous = true), activeUserIT)
        val feedbackId1 = feedbackDao.createFeedback(feedback1, activeUserIT)
        assertEquals(
            listOf(
                Feedback(
                    feedbackId,
                    Date.valueOf(LocalDate.now()),
                    companyId,
                    feedback.feedbackMessage,
                    null
                )
            ), feedbackDao.filterFeedbacks(Filter(isAnonymous = true), activeUserHR)
        )
    }
}
