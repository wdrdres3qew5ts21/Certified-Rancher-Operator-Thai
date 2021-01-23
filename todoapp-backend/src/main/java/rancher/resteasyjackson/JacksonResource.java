package rancher.resteasyjackson;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rancher.resteasyjackson.model.Todo;
import rancher.resteasyjackson.repository.TodoRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonResource {

    private final Set<Quark> quarks = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    private String databaseUrl;

    @Inject
    private TodoRepository todoRepository;
    
    public JacksonResource() {
        quarks.add(new Quark("Up", "The up quark or u quark (symbol: u) is the lightest of all quarks, a type of elementary particle, and a major constituent of matter."));
        quarks.add(new Quark("Strange", "The strange quark or s quark (from its symbol, s) is the third lightest of all quarks, a type of elementary particle."));
        quarks.add(new Quark("Charm", "The charm quark, charmed quark or c quark (from its symbol, c) is the third most massive of all quarks, a type of elementary particle."));
        quarks.add(new Quark("???", null));
    }

    @GET
    public Set<Quark> list() {
        return quarks;
    }
    
    @POST
    @Path("/todo")
    public Response createTodo(Todo todo) {
    	Todo savedTodoFromDatabase = todoRepository.save(todo);
        return Response.status(200).entity(savedTodoFromDatabase).build();
    }

    @GET
    @Path("/todo/{id}")
    public Response getTodoById(@PathParam("id") int id) {
    	Todo todoFromDatabase = todoRepository.findById(id).get();
        return Response.status(200).entity(todoFromDatabase).build();
    }
    
    @DELETE
    @Path("/todo/{id}")
    public Response deleteTodoById(@PathParam("id") int id) {
    	todoRepository.deleteById(id);
        return Response.noContent().build();
    }
    
    @GET
    @Path("/todolist")
    public Response getTodoList() {
    	List<Todo> todoFromDatabase = todoRepository.findAll();
    	return Response.status(200).entity(todoFromDatabase).build();
    }

    @POST
    public Set<Quark> add(Quark quark) {
        quarks.add(quark);
        return quarks;
    }

    @DELETE
    public Set<Quark> delete(Quark quark) {
        quarks.removeIf(existingQuark -> existingQuark.name.contentEquals(quark.name));
        return quarks;
    }

    public static class Quark {
        public String name;
        public String description;

        public Quark() {
        }

        public Quark(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
