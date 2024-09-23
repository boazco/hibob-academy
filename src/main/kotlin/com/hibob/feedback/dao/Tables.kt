package com.hibob.feedback.dao


import com.hibob.academy.utils.JooqTable


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

class EmployeesTable(tablename: String) : JooqTable(tablename) {
    val employeeId = createUUIDField("id")
    val firstName = createVarcharField("first_name")
    val lastName = createVarcharField("last_name")
    val role = createVarcharField("role")
    val department = createVarcharField("department")
    val companyId = createUUIDField("company_id")

    companion object {
        val instance = EmployeesTable("employees")
    }
}

class CompanyTable(tablename: String) : JooqTable(tablename) {
    val companyId = createUUIDField("id")
    val name = createVarcharField("name")

    companion object {
        val instance = CompanyTable("company")
    }
}
