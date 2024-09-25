package com.hibob.feedback.dao

import com.hibob.academy.utils.BobDbTest
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
}