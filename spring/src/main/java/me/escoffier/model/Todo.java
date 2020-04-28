package me.escoffier.model;

import org.hibernate.annotations.Generated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    private String title;

    private boolean completed;

    @Column(name = "ordering")
    private int order;

    @NotNull
    private String owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

//    public static List<Todo> getTodos(String owner) {
//        return list("owner", Sort.by("order"), owner);
//    }
//
//    public static void clearCompleted(String owner) {
//        delete("owner = ?1 and completed = ?2", owner, true);
//    }
//
//    public static Optional<Todo> getTodo(String owner, Long id) {
//        return find("owner = ?1 and id = ?2", owner, id).firstResultOptional();
//    }
}
