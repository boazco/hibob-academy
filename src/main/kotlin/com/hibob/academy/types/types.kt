package com.hibob.academy.types

import java.util.*
import java.time.LocalDateTime
import java.util.UUID

data class Pets(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var type: String,
    var companyId: UUID,
    var dateofArrival: Date
)

data class Owner(
    val id: UUID,
    val companyId: UUID,
    val employeeId: UUID,
    var name: String?,
    var type: String,
    var dateOfArrival: LocalDateTime,
    var firstName: String?,
    var lastName: String?
)