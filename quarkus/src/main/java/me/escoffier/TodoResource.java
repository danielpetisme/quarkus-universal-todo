package me.escoffier;

import io.quarkus.security.identity.SecurityIdentity;
import me.escoffier.model.Todo;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/api")
@Produces("application/json")
@Consumes("application/json")
@RolesAllowed("user")
public class TodoResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    public List<Todo> getAll() {
        return Todo.getTodos(getCurrentUser());
    }

    @GET
    @Path("/{id}")
    public Todo getOne(@PathParam("id") Long id) {
        return Todo.getTodo(getCurrentUser(), id).orElseThrow(
                () ->  new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND)
        );
    }

    @POST
    @Transactional
    public Response create(@Valid Todo item) {
        item.owner = getCurrentUser();
        item.persist();
        return Response.status(Status.CREATED).entity(item).build();
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    public Response update(@Valid Todo todo, @PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        entity.id = id;
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        entity.owner = getCurrentUser();
        return Response.ok(entity).build();
    }

    @DELETE
    @Transactional
    public Response deleteCompleted() {
        Todo.clearCompleted(getCurrentUser());
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteOne(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        entity.delete();
        return Response.noContent().build();
    }

    private String getCurrentUser() {
        return securityIdentity.getPrincipal().getName();
    }

}
