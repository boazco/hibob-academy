package com.hibob.academy.resource

import com.hibob.academy.dao.PetDao
import com.hibob.academy.service.PetService
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import java.awt.PageAttributes

@Component
@Path("/pets")
class PetResource(
    private val petService: PetService
) {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{companyId}/{type}")
    fun petsByType(@PathParam("companyId") companyId: Long, @PathParam("type") type: String): Response {
        return Response.ok(petService.petsByType(type, companyId)).build()
    }



}