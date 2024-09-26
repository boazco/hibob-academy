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
class ResponseDaoTest @Autowired constructor(private val sql: DSLContext){
    private val responseDao = ResponseDao(sql)
    private val responseTable = ResponseDao.ResponseTable.instance
    private val companyId = UUID.randomUUID()
    private val hrId = UUID.randomUUID()
    private val responseInput = ResponseInput(UUID.randomUUID(), "Response for testing", hrId)
    private val activeUser = ActiveUser(hrId, companyId, Role.ADMIN, Department.HR)

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(responseTable).where(responseTable.companyId.eq(companyId)).execute()
    }

    @Test
    fun `create response should return response UUID`(){
        assertTrue(responseDao.createResponse(responseInput, activeUser) is UUID)
    }
}