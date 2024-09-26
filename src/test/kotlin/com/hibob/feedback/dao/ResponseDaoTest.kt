package com.hibob.feedback.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

@BobDbTest
class ResponseDaoTest @Autowired constructor(private val sql: DSLContext){
}