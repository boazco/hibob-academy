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
        return petDao.createPet(pet)
    }

    fun assignOwnerIdToPet(petId: UUID, ownerId: UUID, companyId: Long) {
        if (petDao.assignOwnerIdToPet(listOf(petId), ownerId, companyId) == 0) {
            throw BadRequestException("No pet with that id")
        }
    }

    fun countPetsByType(companyId: Long): Map<String, Int> {
        return petDao.countPetsByType(companyId)
    }

    fun adoptMultiple(pets: List<UUID>, ownerId: UUID, companyId: Long) {
        petDao.assignOwnerIdToPet(pets, ownerId, companyId)
    }

    fun createMultiplePets(pets: List<PetNoId>) {
        petDao.createMultiplePets(pets)
    }
}