package com.hibob.academy.dao

import jakarta.ws.rs.BadRequestException
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.Record
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*

@Component
class OwnerDao(private val sql: DSLContext) {

    private val ownerTable = OwnersTable.instance
    private val petTable = PetsTable.instance

    private val ownerMapper = RecordMapper<Record, Owner> { record ->
        Owner(
            record[ownerTable.id],
            record[ownerTable.companyId],
            record[ownerTable.employeeId],
            record[ownerTable.name]
        )
    }

    fun getOwners(companyId: Long): List<Owner> =
        sql.select(ownerTable.id, ownerTable.companyId, ownerTable.employeeId, ownerTable.name)
            .from(ownerTable)
            .where(ownerTable.companyId.eq(companyId))
            .fetch(ownerMapper)

    fun createOwner(companyId: Long, employeeId: String, name: String): UUID {
        return sql.insertInto(ownerTable)
            .set(ownerTable.id, UUID.randomUUID())
            .set(ownerTable.companyId, companyId)
            .set(ownerTable.employeeId, employeeId)
            .set(ownerTable.name, name)
            .onConflict(ownerTable.companyId, ownerTable.employeeId)
            .doNothing()
            .returning(ownerTable.id)
            .fetchOne()?.let { it[ownerTable.id] }
            ?: throw BadRequestException("Owner with the same company and employee id already exists")
    }

    fun getOwnerByPetId(petId: UUID, companyId: Long): MutableList<Owner> {
        return sql.select(ownerTable.id, ownerTable.companyId, ownerTable.name, ownerTable.employeeId)
            .from(ownerTable)
            .join(petTable)
            .on(ownerTable.id.eq(petTable.ownerId))
            .where(petTable.id.eq(petId))
            .and(ownerTable.companyId.eq(companyId))
            .fetch(ownerMapper)
    }
}