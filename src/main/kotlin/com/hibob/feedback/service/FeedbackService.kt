package com.hibob.feedback.service

import com.hibob.feedback.dao.*
import com.hibob.feedback.dao.ActiveUser
import com.hibob.feedback.dao.Feedback
import com.hibob.feedback.dao.FeedbackInput
import jakarta.ws.rs.BadRequestException
import org.jooq.Condition
import org.springframework.stereotype.Component
import java.util.*

@Component
class FeedbackService(private val feedbackDao: FeedbackDao, private val employeesDao: EmployeesDao) {

    fun createFeedback(feedbackInput: FeedbackInput, activeUser: ActiveUser): UUID {
        return feedbackDao.createFeedback(feedbackInput, activeUser)
    }

    fun getFeedback(feedbackId: UUID, activeUser: ActiveUser): Feedback? {
        val feedback = feedbackDao.getFeedback(feedbackId, activeUser)
        return feedback
    }

    fun getStatus(feedbackId: UUID, activeUser: ActiveUser): Status {
        return feedbackDao.getStatus(feedbackId, activeUser)
    }

    fun changeStatus(feedbackId: UUID, status: Status, activeUser: ActiveUser) {
        if (feedbackDao.changeStatus(feedbackId, status, activeUser) == 0)
            throw BadRequestException("No feedback found  with that id")
    }

    fun filterFeedbacks(condition: Filter, activeUser: ActiveUser): List<Feedback>? {
        val table = FeedbackTable.instance
        val employeeTable = EmployeesDao.EmployeesTable.instance
        val conditionList = ArrayList<Condition>()
        var departmentCondition = false
        condition.isAnonymous?.let { conditionList.add(if (condition.isAnonymous) table.employeeId.isNotNull else table.employeeId.isNull) }
        condition.date?.let { conditionList.add(table.creationDate.gt(condition.date)) }
        condition.department?.let {
            departmentCondition = true
            conditionList.add(employeeTable.department.eq(condition.department.toString().uppercase()))
        }
        return feedbackDao.filterFeedbacks(conditionList, departmentCondition, activeUser)
    }
}