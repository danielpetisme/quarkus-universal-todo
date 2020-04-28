package me.escoffier;

import me.escoffier.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByOwnerAndId(String owner, Long id);

    List<Todo> findAllByOwnerIgnoreCase(String owner);

    @Modifying
    @Query("delete from Todo t where UPPER(t.owner) = UPPER(?1) and t.completed = true")
    void clearCompleted(String owner);

}
