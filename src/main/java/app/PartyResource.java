package app;

import com.ferrylink.tmf.filter.mongodb.TmfApiBase;
import com.ferrylink.tmf.filter.mongodb.runtime.params.PaginateQuery;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBodySchema;

import java.util.Map;
import java.util.UUID;

/**
 * PartyResource provides RESTful endpoints for managing parties.
 * 
 * This API allows clients to create, retrieve, list, and delete party entities.
 * Each party is identified by a unique ID and supports flexible metadata.
 * 
 * @summary Party Resource API
 * @tag Party
 */
@ApplicationScoped
@Path("/api/parties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PartyResource extends TmfApiBase<Party, PartyRepo> {

    public PartyResource(@Context HttpHeaders headers, @Context UriInfo uriInfo, @Context Request request) {
        this.init(headers, uriInfo, request);
    }

    /**
     * Creates a new party.
     *
     * @param data A JSON payload containing the party details.
     * @return A Uni<Response> with the status and details of the created party.
     */
    @POST
    @Operation(
        summary = "Create a new party",
        description = "Adds a new party to the database using the provided JSON payload. Returns the created party's details."
    )
    @Parameters({
        @Parameter(name = "data", description = "The JSON payload containing the party details", required = true)
    })
    @RequestBody(
      content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Party.class))
    )
    @RequestBodySchema(Party.class)
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Party created successfully",
                     content = @Content(schema = @Schema(implementation = Party.class))),
        @APIResponse(responseCode = "500", description = "Failed to create the party")
    })
    public Uni<Response> createParty(Map<String, Object> data) {
        return super.create(data)
            .onFailure()
            .recoverWithItem(throwable ->
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create party", "details", throwable.getMessage()))
                    .build());
    }

    /**
     * Retrieves a party by its ID.
     *
     * @param id The UUID of the party to retrieve.
     * @return A Uni<Response> with the party details if found, or an error if not.
     */
    @GET
    @Path("/{id}")
    @Operation(
        summary = "Retrieve a party by ID",
        description = "Fetches a party using its unique ID. Returns the party details if found."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Party found", 
                     content = @Content(schema = @Schema(implementation = Party.class))),
        @APIResponse(responseCode = "404", description = "Party not found"),
        @APIResponse(responseCode = "500", description = "Failed to retrieve the party")
    })
    public Uni<Response> getPartyById(@PathParam("id") String id) {
        return this.getEntityById(UUID.fromString(id))
            .invoke(party -> Log.info("Party found: " + party))
            .onItem().ifNull().failWith(() -> new NotFoundException("Party not found"))
            .map(party -> Response.ok(party).build())
            .onFailure(NotFoundException.class)
            .recoverWithItem(throwable -> Response.status(Response.Status.NOT_FOUND).entity(throwable.getMessage()).build())
            .onFailure()
            .recoverWithItem(throwable ->
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve party", "details", throwable.getMessage()))
                    .build());
    }

    /**
     * Lists all parties with optional pagination.
     *
     * @param paginateQuery Pagination parameters.
     * @return A Uni<Response> containing the list of parties.
     */
    @GET
    @Operation(
        summary = "List all parties",
        description = "Retrieves all parties, optionally paginated using query parameters."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "List of parties returned successfully", 
                     content = @Content(schema = @Schema(implementation = PaginateQuery.class))),
        @APIResponse(responseCode = "500", description = "Failed to retrieve parties")
    })
    public Uni<Response> listParties(@Valid @BeanParam PaginateQuery paginateQuery) {
        return super.find(paginateQuery);
    }

    /**
     * Deletes a party by its ID.
     *
     * @param id The UUID of the party to delete.
     * @return A Uni<Response> indicating the result of the operation.
     */
    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Delete a party by ID",
        description = "Removes a party from the database using its unique ID."
    )
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Party deleted successfully"),
        @APIResponse(responseCode = "404", description = "Party not found"),
        @APIResponse(responseCode = "500", description = "Failed to delete the party")
    })
    public Uni<Response> deletePartyById(@PathParam("id") String id) {
        return super.delete(UUID.fromString(id))
            .onItem().transform(deleted -> Response.noContent().build())
            .onFailure(NotFoundException.class)
            .recoverWithItem(throwable -> Response.status(Response.Status.NOT_FOUND).entity(throwable.getMessage()).build())
            .onFailure()
            .recoverWithItem(throwable ->
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete party", "details", throwable.getMessage()))
                    .build());
    }

    /**
     * Deletes all parties.
     *
     * @return A Uni<Response> indicating the result of the operation.
     */
    @DELETE
    @Operation(
        summary = "Delete all parties",
        description = "Removes all parties from the database."
    )
    @APIResponses({
        @APIResponse(responseCode = "204", description = "All parties deleted successfully"),
        @APIResponse(responseCode = "500", description = "Failed to delete parties")
    })
    public Uni<Response> deleteAllParties() {
        return super.delete()
            .onItem().transform(deleted -> Response.noContent().build())
            .onFailure()
            .recoverWithItem(throwable ->
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete parties", "details", throwable.getMessage()))
                    .build());
    }

    @PATCH
    @Path("/{id}")
    @Consumes("application/merge-patch+json")
    @Operation(
        summary = "Update a party by ID",
        description = "Updates a party using its unique ID. Returns the updated party details."
    )
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Party updated successfully", 
                     content = @Content(schema = @Schema(implementation = Party.class))),
        @APIResponse(responseCode = "404", description = "Party not found"),
        @APIResponse(responseCode = "500", description = "Failed to update the party")
    })
    public Uni<Response> updatePartyById(@PathParam("id") String id, Map<String, Object> data) {
        return super.patch(UUID.fromString(id), data)
            .onFailure(NotFoundException.class)
            .recoverWithItem(throwable -> Response.status(Response.Status.NOT_FOUND).entity(Map.of("details", "Party not found for ID : %s".formatted(id), "error", throwable.getMessage())).build())
            .onFailure()
            .recoverWithItem(throwable ->
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to update party", "details", throwable.getMessage()))
                    .build());
    }
}
