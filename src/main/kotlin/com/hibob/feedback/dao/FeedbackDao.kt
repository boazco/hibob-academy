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
        val query = sql.select()
            .from(feedbackTables)
        val queryWIthJoinAndDepartmentCondition = addDepartmentToQuery(query, filter, activeUser)
        val queryWithAnonymousCondition = addAnonymousToQuery(queryWIthJoinAndDepartmentCondition, filter)
        val queryWithAllConditions = addDateToQuery(queryWithAnonymousCondition, filter)
        return queryWithAllConditions.fetch(feedbackMapper)
    }

    private fun addDepartmentToQuery(
        query:
        SelectJoinStep<Record>,
        filter: Filter,
        activeUser: ActiveUser,
    ):
            SelectConditionStep<Record> {
        val employeeTable = EmployeesDao.EmployeesTable.instance

        return filter.department?.let {
            filter.isAnonymous?.let {
                if (!filter.isAnonymous) {
                    query.leftJoin(employeeTable).on(employeeTable.employeeId.eq(feedbackTables.employeeId))
                        .where(employeeTable.department.eq(filter.department.toString().uppercase()))
                        .and(feedbackTables.companyId.eq(activeUser.companyId))
                } else null
            }
        } ?: query.where(feedbackTables.companyId.eq(activeUser.companyId))
    }

    private fun addAnonymousToQuery(
        query:
        SelectConditionStep<Record>, filter: Filter
    ): SelectConditionStep<Record> {
        return filter.isAnonymous?.let { query.and(if (filter.isAnonymous) feedbackTables.employeeId.isNull else feedbackTables.employeeId.isNotNull) }
            ?: query
    }

    private fun addDateToQuery(
        query:
        SelectConditionStep<Record>, filter: Filter
    ): SelectConditionStep<Record> {
        return filter.date?.let { query.and(feedbackTables.creationDate.gt(filter.date)) } ?: query
    }
}
