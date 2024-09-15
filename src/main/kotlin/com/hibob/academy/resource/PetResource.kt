package com.hibob.academy.resource

import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetDao
import com.hibob.academy.dao.PetNoId
import com.hibob.academy.service.PetService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import java.awt.PageAttributes
import java.util.*

@Component
@Path("/pets")
class PetResource(
    private val petService: PetService
) {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/byType/{companyId}/{type}")
    fun petsByType(@PathParam("companyId") companyId: Long, @PathParam("type") type: String): Response {
        return Response.ok(petService.petsByType(type, companyId)).build()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/byOwner/{companyId}/{ownerId}")
    fun getPetsByOwnerId(@PathParam("companyId") ownerId: UUID, @PathParam("ownerId") companyId: Long): Response {
        return Response.ok(petService.getPetsByOwnerId(ownerId, companyId)).build()
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createPet(@RequestBody pet: PetNoId): Response {
        val petId = petService.createPet(pet)
        if (petId != null) {
            return Response.ok(petId).build()
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{petId}/{ownerId}")
    fun assignOwnerIdToPet(@PathParam("petId") petId: UUID, @PathParam("ownerId") ownerId: UUID): Response {
        return Response.ok(petService.assignOwnerIdToPet(petId, ownerId)).build()
    }

}