package com.hibob.academy.service

import com.hibob.academy.dao.*
import jakarta.ws.rs.BadRequestException
import org.springframework.stereotype.Component
import java.util.*

@Component
class PetService(private val petDao: PetDao) {

    fun petsByType(type: String, companyId: Long): List<Pet> {
        return petDao.petsByType(type, companyId)
    }

    fun getPetsByOwnerId(ownerId: UUID, companyId: Long): List<Pet> {
        return petDao.getPetsByOwnerId(ownerId, companyId)
    }

    fun createPet(pet: PetNoId): UUID {
        val petId = petDao.createPet(pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownersId)
        if (petId != null) {
            return petId
        }
        else{
            throw BadRequestException()
        }
    //NEED TO CHANGE the dao create owner so it gets PetNoId (AFTER PREV PR APPROVED)
    }

    fun assignOwnerIdToPet(petId: UUID, ownerId: UUID) {
        petDao.assignOwnerIdToPet(petId, ownerId)
    }
}