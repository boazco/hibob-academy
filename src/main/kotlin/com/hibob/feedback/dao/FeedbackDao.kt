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
            query.join(employeeTable)
                .on(employeeTable.employeeId.eq(feedbackTables.employeeId))
        } ?: query


        val queryStage3 = queryStage2.where(feedbackTables.companyId.eq(activeUser.companyId))
        val queryStage4 =
            filter.isAnonymous?.let { queryStage3.and(if (filter.isAnonymous) feedbackTables.employeeId.isNotNull else feedbackTables.employeeId.isNull) }
                ?: queryStage3

        val queryStage5 =
            filter.department?.let { queryStage4.and(employeeTable.department.eq(it.toString().uppercase())) }
                ?: queryStage4

        val queryStage6 =

//        val queryStage4 = filter.
//        val lastQuery = conditionQuery.and(conditions.reduce { acc, condition -> acc.and(condition) })
//        return lastQuery.fetch(feedbackMapper)
    }

    /*
            condition.isAnonymous?.let { conditionList.add(if (condition.isAnonymous) table.employeeId.isNotNull else table.employeeId.isNull) }
        condition.date?.let { conditionList.add(table.creationDate.gt(condition.date)) }
        condition.department?.let {
            departmentCondition = true
            conditionList.add(employeeTable.department.eq(condition.department.toString().uppercase()))
        }
     */

}
