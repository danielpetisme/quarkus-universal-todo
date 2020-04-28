package me.escoffier;

import me.escoffier.model.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
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
    public List<Todo> getAll(Principal principal) {
        return todoRepository.findAllByOwnerIgnoreCase(principal.getName());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Todo getOne(@PathVariable("id") Long id, Principal principal) {
        return todoRepository.findByOwnerAndId(principal.getName(), id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist.")
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo item, Principal principal) throws URISyntaxException {
        item.setOwner(principal.getName());
        Todo result = todoRepository.save(item);
        return ResponseEntity.created(new URI("/api/" + result.getId())).body(result);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Todo> update(@Valid @RequestBody Todo todo, @PathVariable("id") Long id, Principal principal) {
        Todo entity = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        entity.setId(id);
        entity.setCompleted(todo.isCompleted());
        entity.setOrder(todo.getOrder());
        entity.setTitle(todo.getTitle());
        entity.setOwner(principal.getName());
        Todo result = todoRepository.save(entity);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteCompleted(Principal principal) {
        todoRepository.clearCompleted(principal.getName());
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
}
