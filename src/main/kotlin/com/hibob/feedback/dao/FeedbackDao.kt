package com.hibob.feedback.dao

import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.springframework.stereotype.Repository
import org.jooq.Record
import java.util.*

@Repository
class FeedbackDao(private val sql: DSLContext) {
    private val feedbackTables = FeedbackTable.instance

    private val feedbackMapper = RecordMapper<Record, Feedback> { record ->
        Feedback(
            record[feedbackTables.feedbackId],
            record[feedbackTables.creationDate],
            record[feedbackTables.companyId],
            record[feedbackTables.feedbackMessage]
        )
    }

    fun createFeedback(feedback: Feedback, activeUser: ActiveUser): UUID {
        return sql.insertInto(feedbackTables)
            .set(feedbackTables.feedbackId, feedback.id)
            .set(feedbackTables.employeeId, activeUser.employeeId)
            .set(feedbackTables.creationDate, feedback.creationDate)
            .set(feedbackTables.companyId, feedback.companyId)
            .set(feedbackTables.status, Status.UNREVIEWED.toString())
            .set(feedbackTables.feedbackMessage, feedback.feedbackMessage)
            .returning(feedbackTables.feedbackId)
            .fetchOne()!![feedbackTables.feedbackId]
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        return sql.select()
            .from(feedbackTables)
            .where(feedbackTables.feedbackId.eq(feedbackId))
            .and(feedbackTables.companyId.eq(activeUser.companyId))
            .fetchOne(feedbackMapper)
    }
}