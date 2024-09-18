package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Date
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@BobDbTest
class PetDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val petDao = PetDao(sql)
    private val companyId = Random.nextLong()
    private val table = PetsTable.instance

    private val ownerId = UUID.randomUUID()
    private val jerry = PetNoId("Jerry", "Dog", companyId, Date.valueOf(LocalDate.now()), ownerId)
    private val johans = PetNoId("Johans", "Dog", companyId, Date.valueOf(LocalDate.now()), ownerId)
    private val mitzi = PetNoId("mitzi", "Cat", companyId, Date.valueOf(LocalDate.now()), ownerId)

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }

    @Test
    fun `get pets by type`() {
        val petId = petDao.createPet(jerry)
        assertEquals(
            listOf(
                Pet(
                    petId,
                    "Jerry",
                    "Dog",
                    companyId,
                    Date.valueOf(LocalDate.now()),
                    ownerId
                )
            ),
            petDao.petsByType(type = "Dog", companyId)
        )
    }

    @Test
    fun `dont get pet with different type`() {
        petDao.createPet(
            PetNoId(
                name = "Jerry",
                type = "Dog",
                companyId = companyId,
                dateOfArrival = Date.valueOf(LocalDate.now()),
                ownerId = null
            )
        )
        assertEquals(emptyList<Pet>(), petDao.petsByType(type = "Cat", companyId))
    }


    @Test
    fun `get pets by type with multiple pets with different type`() {
        val petId1 = petDao.createPet(jerry)
        val petId2 = petDao.createPet(johans)
        petDao.createPet(mitzi)
        assertEquals(
            listOf(
                Pet(
                    petId1,
                    name = "Jerry",
                    type = "Dog",
                    companyId = companyId,
                    dateOfArrival = Date.valueOf(LocalDate.now()),
                    ownerId = ownerId
                ),
                Pet(
                    petId2,
                    name = "Johans",
                    type = "Dog",
                    companyId = companyId,
                    dateOfArrival = Date.valueOf(LocalDate.now()),
                    ownerId = ownerId
                )
            ), petDao.petsByType(type = "Dog", companyId)
        )
    }

    @Test
    fun `get pets by ownerId with few pets`() {
        val petId1 = petDao.createPet(jerry)
        val petId2 = petDao.createPet(johans)
        val petId3 = petDao.createPet(mitzi)
        assertEquals(
            listOf(
                Pet(
                    petId1,
                    "Jerry",
                    "Dog",
                    companyId,
                    Date.valueOf(LocalDate.now()),
                    ownerId
                ), Pet(
                    petId2,
                    "Johans",
                    "Dog",
                    companyId,
                    Date.valueOf(LocalDate.now()),
                    ownerId
                ), Pet(
                    petId3,
                    "mitzi",
                    "Cat",
                    companyId,
                    Date.valueOf(LocalDate.now()),
                    ownerId
                )
            ), petDao.getPetsByOwnerId(ownerId, companyId)
        )
    }

    @Test
    fun `get pets by ownerId with few owners`() {
        val petId = petDao.createPet(jerry)
        petDao.createPet(PetNoId("buddy", "Cow", companyId, Date.valueOf(LocalDate.now()), null))
        petDao.createPet(PetNoId("micky", "Dog", companyId, Date.valueOf(LocalDate.now()), null))
        assertEquals(
            listOf(
                Pet(
                    petId,
                    "Jerry",
                    "Dog",
                    companyId,
                    Date.valueOf(LocalDate.now()),
                    ownerId
                )
            ),
            petDao.getPetsByOwnerId(ownerId, companyId)
        )
    }

    @Test
    fun `updating pets owner id`() {
        val ownerId = UUID.randomUUID()
        val petId = petDao.createPet(jerry)
        petDao.assignOwnerIdToPet(petId, ownerId)
        assertEquals(
            listOf(
                Pet(
                    petId,
                    "Jerry",
                    "Dog",
                    companyId,
                    Date.valueOf(LocalDate.now()),
                    ownerId
                )
            ),
            petDao.getPetsByOwnerId(ownerId, companyId)
        )

    }

    @Test
    fun `count by type`() {
        petDao.createPet(jerry)
        petDao.createPet(johans)
        petDao.createPet(mitzi)
        petDao.createPet(PetNoId("hh", "Cat", companyId, Date.valueOf(LocalDate.now()), null))
        assertEquals(mapOf("Dog" to 2, "Cat" to 2), petDao.countPetsByType(companyId))
    }

    @Test
    fun `add multiple pets using batch`() {
        val petsList = listOf(jerry, johans, mitzi)
        petDao.createMultiplePets(petsList)
        val petsByCompany = petDao.getPetsByCompanyId(companyId)
        assertEquals(listOf("Jerry", "Johans", "mitzi"), petsByCompany.map { it.name })
        assertEquals(listOf("Dog", "Dog", "Cat"), petsByCompany.map { it.type })
    }
}