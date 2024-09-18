package com.hibob.academy.service

import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetDao
import com.hibob.academy.dao.PetNoId
import jakarta.ws.rs.BadRequestException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.sql.Date
import java.time.LocalDate
import java.util.*

class PetServiceTest {
    private val petDaoMock = mock<PetDao>()
    private val service = PetService(petDaoMock)

    @Test
    fun `get pet by type`() {
        val pet = Pet(UUID.randomUUID(), "jerry", "Dog", 9, Date.valueOf(LocalDate.now()), null)
        whenever(petDaoMock.petsByType("Dog", 9)).thenReturn(listOf(pet))
        assertEquals(listOf(pet), service.petsByType("Dog", 9))
    }

    @Test
    fun `get pet by id`() {
        val ownerId = UUID.randomUUID()
        val pet = Pet(UUID.randomUUID(), "jerry", "Dog", 9, Date.valueOf(LocalDate.now()), ownerId)
        whenever(petDaoMock.getPetsByOwnerId(ownerId, 9)).thenReturn(listOf(pet))
        assertEquals(listOf(pet), service.getPetsByOwnerId(ownerId, 9))
    }

    @Test
    fun `create new pet`() {
        val petId = UUID.randomUUID()
        val pet = PetNoId("jerry", "Dog", 9, Date.valueOf(LocalDate.now()), null)
        whenever(petDaoMock.createPet(PetNoId( pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownerId))).thenReturn(
            petId
        )
        assertEquals(petId, service.createPet(pet))
    }

    @Test
    fun `update pet owner id- sucsess `() {
        val pet = PetNoId("jerry", "Dog", 9, Date.valueOf(LocalDate.now()), null)
        val ownerId = UUID.randomUUID()
        whenever(petDaoMock.assignOwnerIdToPet(any() ,any())).thenReturn(1)
        assertDoesNotThrow{service.assignOwnerIdToPet(UUID.randomUUID(), ownerId)}
    }

    @Test
    fun `update pet owner id- fails `() {
        val ownerId = UUID.randomUUID()
        val notPetId = UUID.randomUUID()
        whenever(petDaoMock.assignOwnerIdToPet(notPetId, ownerId)).thenReturn(0)
        assertEquals(
            "No pet with that id",
            org.junit.jupiter.api.assertThrows<BadRequestException> {
                service.assignOwnerIdToPet(
                    notPetId,
                    ownerId
                )
            }.message
        )
    }

    @Test
    fun `count pets by type`() {
        whenever(petDaoMock.countPetsByType(9)).thenReturn(mapOf("Dog" to 2, "Cat" to 3))
        assertEquals(mapOf("Dog" to 2, "Cat" to 3), service.countPetsByType(9))
    }

    @Test
    fun `update multiple pets owners`(){
        val ownerId = UUID.randomUUID()
        val petId = UUID.randomUUID()
        whenever(petDaoMock.assignOwnerIdToPet(petId, ownerId)).thenReturn(1)
        val petsId = listOf(petId, petId, petId, petId, petId)
        assertEquals(5, service.adoptMultiple(petsId, ownerId, 9))
    }

    @Test
    fun `not updating owners when the list is empty`(){
        assertEquals(0, service.adoptMultiple(listOf<UUID>(), ownerId = UUID.randomUUID(), 9))
    }
}