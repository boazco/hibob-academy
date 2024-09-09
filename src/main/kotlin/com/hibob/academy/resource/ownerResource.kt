package com.hibob.academy.resource

import com.hibob.academy.types.Pets
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import jakarta.ws.rs.core.MediaType
import org.springframework.web.bind.annotation.RequestBody

@Controller
@Path("api/owners")
@Produces(MediaType.APPLICATION_JSON)
class OwnerResource {

    @POST
    @Path("/{Id}/type/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    fun createPetV1(@RequestBody pets: Pets): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("POST OK").build()
    }

    @POST
    @Path("/{Id}/type/v2")
    @Consumes(MediaType.APPLICATION_JSON)
    fun createPetV2(@RequestBody pets: Pets): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("POST OK").build()
    }

    @GET
    @Path("/{Id}/type/v1")
    fun getPetTypeV1(@PathParam("Id") id: String): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("GET OK").build()
    }

    @GET
    @Path("/{Id}/type/v2")
    fun getPetTypeV2(@PathParam("Id") id: String): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("GET OK").build()
    }

    @PUT
    @Path("/{Id}/type/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updatePetTypeV1(@PathParam("Id") id: String, petType: String): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("PUT OK").build()
    }

    @PUT
    @Path("/{Id}/type/v2")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updatePetTypeV2(@PathParam("Id") id: String, petType: String): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("PUT OK").build()
    }

    @DELETE
    @Path("/{Id}/type")
    fun deletePetType(@PathParam("Id") id: String): Response {
        Response.status(Response.Status.OK).build()
        return Response.ok("DELETE OK").build()
    }
}