package com.hibob.academy.service

import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.EmployeesDao
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Component
class SessionService(private val employeesDao: EmployeesDao) {
    companion object {
        const val SECRET_KEY =
            "ndjiaqsfghouhm24vvycgjhfuytuytgiyusasfdghjkhgfdfgyuiuytrfftyuiuytrtyuihttixicuvb785v68568ig5buitui8onkgy7498cqdehqiwfhbui5p3hqugo5hr"
    }

    fun createJwtToken(activeUser: ActiveUser): String {
        //val employee = employeesDao.getEmployeeById(activeUser)
        // employee ?: throw exception
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("employeeId", activeUser.employeeId)
            .claim("companyId", activeUser.companyId)
            .claim("role", activeUser.role)
            .claim("department", activeUser.department)
            //TO DO: verify that the employee exists in this company (check in DB) and get his role from DB and add claim
            .setExpiration(Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

}