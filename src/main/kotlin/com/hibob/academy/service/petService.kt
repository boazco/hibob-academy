package com.hibob.academy.service

import com.hibob.academy.dao.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class PetService(private val petDao: PetDao){

    fun petsByType(type: String, companyId: Long): List<Pet> {
        return petDao.petsByType(type, companyId)
    }

    fun getPetsByOwnerId(ownerId: UUID, companyId: Long): List<Pet> {
        return petDao.getPetsByOwnerId(ownerId, companyId)
    }

    fun createPet(pet: Pet): UUID? {
        return petDao.createPet(pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownersId)
        //NEED TO CHANGE the dao create owner so it gets PetNoId (AFTER PREV PR APPROVED)
    }

    fun assignOwnerIdToPet(petId: UUID, ownerId: UUID){
        petDao.assignOwnerIdToPet(petId, ownerId)
    }
}