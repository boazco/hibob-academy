package com.hibob.feedback.dao

import com.hibob.academy.utils.JooqTable
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ResponseDao(private val sql: DSLContext) {

    class ResponseTable(tablename: String) : JooqTable(tablename) {
        val responseId = createUUIDField("id")
        val employeeId = createUUIDField("employee_id")
        val creationDate = createDateField("creation_date")
        val companyId = createUUIDField("company_id")
        val feedbackId = createUUIDField("feedback_id")
        val responseMessage = createVarcharField("response_message")

        companion object {
            val instance = ResponseTable("response")
        }
    }

    private val responseTable = ResponseTable.instance
    private val feedbackTable = FeedbackTable.instance

    fun createResponse(response: ResponseInput, activeUser: ActiveUser): UUID {
        return sql.insertInto(responseTable)
            .set(responseTable.employeeId, response.employeeId)
            .set(responseTable.companyId, activeUser.companyId)
            .set(responseTable.feedbackId, response.feedbackId)
            .set(responseTable.responseMessage, response.responseMessage)
            .returning(responseTable.responseId)
            .fetchOne()!![responseTable.responseId]
    }


}