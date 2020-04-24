package me.escoffier;

import me.escoffier.model.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RolesAllowed({"ROLE_USER"})
public class TodoResource {

    private final TodoRepository todoRepository;

    public TodoResource(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Todo> getAll() {
        return todoRepository.findAllByOwnerIgnoreCase(getCurrentUser());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Todo getOne(@PathVariable("id") Long id) {
        return todoRepository.findByOwnerAndId(getCurrentUser(), id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist.")
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo item) throws URISyntaxException {
        item.setOwner(getCurrentUser());
        Todo result = todoRepository.save(item);
        return ResponseEntity.created(new URI("/api/" + result.getId())).body(result);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Todo> update(@Valid @RequestBody Todo todo, @PathVariable("id") Long id) {
        Todo entity = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        entity.setId(id);
        entity.setCompleted(todo.isCompleted());
        entity.setOrder(todo.getOrder());
        entity.setTitle(todo.getTitle());
        entity.setOwner(getCurrentUser());
        Todo result = todoRepository.save(entity);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteCompleted() {
        todoRepository.clearCompleted(getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteOne(@PathVariable("id") Long id) {
        Todo entity = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        todoRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        if (principal instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal) principal).getName();
        }
        return principal.toString();
    }

}
