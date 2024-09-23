package com.hibob.feedback.dao

import jakarta.ws.rs.BadRequestException
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class EmployeesDao(private val sql: DSLContext) {
    private val employeesTable = EmployeesTable.instance

    fun createEmployee(firstName: String, lastName: String, role: String, companyId: UUID, department: String): UUID{
        return sql.insertInto(employeesTable)
            .set(employeesTable.employeeId, UUID.randomUUID())
            .set(employeesTable.firstName, firstName)
            .set(employeesTable.lastName, lastName)
            .set(employeesTable.role, role)
            .set(employeesTable.companyId, companyId)
            .set(employeesTable.department, department)
            .returning(employeesTable.employeeId)
            .fetchOne()!![employeesTable.employeeId]
    }

    fun getDepartmentById(employeeId: UUID, companyId: UUID): String {
        return sql.select(employeesTable.department)
            .from(employeesTable)
            .where(employeesTable.employeeId.eq(employeeId))
            .and(employeesTable.companyId.eq(companyId))
            .fetchOne()?.let { it[employeesTable.department] }
            ?: throw BadRequestException("No employee with that id")
    }

    fun getRoleById(employeeId: UUID, companyId: UUID): String {
        return sql.select(employeesTable.role)
            .from(employeesTable)
            .where(employeesTable.employeeId.eq(employeeId))
            .and(employeesTable.companyId.eq(companyId))
            .fetchOne()?.let { it[employeesTable.role] }
            ?: throw BadRequestException("No employee with that id")
    }
}
