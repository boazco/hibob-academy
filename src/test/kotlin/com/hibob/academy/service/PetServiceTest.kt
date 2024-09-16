package com.hibob.academy.service

import com.hibob.academy.dao.PetDao
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class PetServiceTest{
    private val petDaoMock = mock<PetDao>()
    private val service = PetService(petDaoMock)

    @Test
    fun `get pet by type`(){
        when(petDaoMock.getPe)
    }
}