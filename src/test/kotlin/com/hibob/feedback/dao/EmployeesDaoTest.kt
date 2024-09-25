package com.hibob.feedback.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.ws.rs.BadRequestException
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@BobDbTest
class EmployeesDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val employeesDao = EmployeesDao(sql)
    private val employeesTable = EmployeesDao.EmployeesTable.instance
    private val companyId = UUID.randomUUID()

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(employeesTable).where(employeesTable.companyId.eq(companyId)).execute()
    }

    @Test
    fun `create new employee`() {
        val employeeId = employeesDao.createEmployee("Hi", "Bob", "admin", companyId, "HR")
        assertTrue(employeeId is UUID)
    }

    @Test
    fun `get employee`() {
        val employeeId = employeesDao.createEmployee("Hi", "Bob", "Admin", companyId, "HR")
        assertEquals(
            Employee(employeeId, companyId, Role.ADMIN, Department.HR),
            employeesDao.getEmployeeByActiveUser(ActiveUser(employeeId, companyId, Role.ADMIN, Department.HR))
        )

    }


    @Test
    fun `throwing exception when no employee exists`() {
        assertEquals(
            "No employee with that id",
            org.junit.jupiter.api.assertThrows<BadRequestException> {
                employeesDao.getEmployeeByActiveUser(ActiveUser(UUID.randomUUID(), companyId, Role.EMPLOYEE, Department.HR))
            }.message
        )
    }
}