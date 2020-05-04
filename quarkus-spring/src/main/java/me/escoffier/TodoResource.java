package me.escoffier;

import io.quarkus.security.identity.SecurityIdentity;
import me.escoffier.model.Todo;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RolesAllowed({"user"})
public class TodoResource {

    private final TodoRepository todoRepository;

    @Inject
    SecurityIdentity securityIdentity;

    public TodoResource(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    @ResponseBody
    public List<Todo> getAll() {
        return todoRepository.findAllByOwnerIgnoreCase(getCurrentUser());
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public Todo getOne(@PathVariable("id") Long id) {
        return todoRepository.findByOwnerAndId(getCurrentUser(), id).orElseThrow(
                () -> new ResponseServerStatusException("Todo with id of " + id + " does not exist.", 404)
        );
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo item) throws URISyntaxException {
        item.setOwner(getCurrentUser());
        Todo result = todoRepository.save(item);
        return ResponseEntity.created(new URI("/api/" + result.getId())).body(result);
    }

    @PatchMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Todo> update(@Valid @RequestBody Todo todo, @PathVariable("id") Long id) {
        Todo entity = todoRepository.findByOwnerAndId(getCurrentUser(), id)
                .orElseThrow(() -> new ResponseServerStatusException("Todo with id of " + id + " does not exist.", 404));
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
                .orElseThrow(() -> new ResponseServerStatusException("Todo with id of " + id + " does not exist.", 404));
        todoRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUser() {
        return securityIdentity.getPrincipal().getName();
    }
}
