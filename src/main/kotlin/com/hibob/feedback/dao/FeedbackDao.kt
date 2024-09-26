package com.hibob.feedback.dao


import com.hibob.academy.utils.JooqTable
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.*
import org.springframework.stereotype.Repository
import java.util.*


@Repository
class FeedbackDao(private val sql: DSLContext) {

    class FeedbackTable(tablename: String) : JooqTable(tablename) {
        val feedbackId = createUUIDField("id")
        val employeeId = createUUIDField("employee_id")
        val creationDate = createDateField("creation_date")
        val companyId = createUUIDField("company_id")
        val status = createVarcharField("status")
        val feedbackMessage = createVarcharField("feedback_message")

        companion object {
            val instance = FeedbackTable("feedback")
        }
    }


    private val feedbackTables = FeedbackTable.instance


    private val feedbackMapper = RecordMapper<Record, Feedback> { record ->
        Feedback(
            record[feedbackTables.feedbackId],
            record[feedbackTables.creationDate],
            record[feedbackTables.companyId],
            record[feedbackTables.feedbackMessage],
            record[feedbackTables.employeeId] ?: null
        )
    }


    fun createFeedback(feedback: FeedbackInput, activeUser: ActiveUser): UUID {
        return sql.insertInto(feedbackTables)
            .set(feedbackTables.employeeId, if (feedback.isAnonymous) null else activeUser.employeeId)
            .set(feedbackTables.companyId, activeUser.companyId)
            .set(feedbackTables.status, Status.UNREVIEWED.toString())
            .set(feedbackTables.feedbackMessage, feedback.feedbackMessage)
            .returning(feedbackTables.feedbackId)
            .fetchOne()!![feedbackTables.feedbackId]
    }


    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback {
        return sql.select()
            .from(feedbackTables)
            .where(feedbackTables.feedbackId.eq(feedbackId))
            .and(feedbackTables.companyId.eq(activeUser.companyId))
            .fetchOne(feedbackMapper)
            ?: throw BadRequestException("Feedback not found")
    }

    fun getStatus(feedbackId: UUID, activeUser: ActiveUser): Status {
        return sql.select(feedbackTables.status)
            .from(feedbackTables)
            .where(feedbackTables.feedbackId.eq(feedbackId))
            .and(feedbackTables.companyId.eq(activeUser.companyId))
            .and(feedbackTables.employeeId.eq(activeUser.employeeId))
            .fetchOne()?.let { Status.valueOf(it[feedbackTables.status].toString().uppercase()) }
            ?: throw NotFoundException("feedback not found")
    }

    fun changeStatus(feedbackId: UUID, status: Status, activeUser: ActiveUser): Int {
        return sql.update(feedbackTables)
            .set(feedbackTables.status, status.toString())
            .where(feedbackTables.feedbackId.eq(feedbackId))
            .and(feedbackTables.companyId.eq(activeUser.companyId))
            .execute()
    }


    fun filterFeedbacks(filter: Filter, activeUser: ActiveUser): List<Feedback>? {
        val employeeTable = EmployeesDao.EmployeesTable.instance
        val query = sql.select()
            .from(feedbackTables)

        val queryStage2 = filter.department?.let {
            filter.isAnonymous?.let {
                if (!filter.isAnonymous) {
                    query.join(employeeTable).on(employeeTable.employeeId.eq(feedbackTables.employeeId))
                } else query
            } ?: query
        } ?: query


        val queryStage3 = queryStage2.where(feedbackTables.companyId.eq(activeUser.companyId))
        val queryStage4 =
            filter.isAnonymous?.let { queryStage3.and(if (filter.isAnonymous) feedbackTables.employeeId.isNull else feedbackTables.employeeId.isNotNull) }
                ?: queryStage3

        val queryStage5 =
            filter.department?.let { queryStage4.and(employeeTable.department.eq(it.toString().uppercase())) }
                ?: queryStage4

        val queryStage6 =
            filter.date?.let { queryStage5.and(feedbackTables.creationDate.gt(filter.date)) } ?: queryStage5
        return queryStage6.fetch(feedbackMapper)
    }

}
