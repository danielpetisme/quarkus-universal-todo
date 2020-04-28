package me.escoffier.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Entity
public class Todo extends PanacheEntity  {

    @NotBlank
    public String title;

    public boolean completed;

    @Column(name = "ordering")
    public int order;

    @NotNull
    public String owner;

    public static List<Todo> getTodos(String owner) {
        return list("owner", Sort.by("order"), owner);
    }

    public static void clearCompleted(String owner) {
        delete("owner = ?1 and completed = ?2", owner, true);
    }

    public static Optional<Todo> getTodo(String owner, Long id) {
        return find("owner = ?1 and id = ?2", owner, id).firstResultOptional();
    }
}
