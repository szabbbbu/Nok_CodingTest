package robert.szabo.imdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import robert.szabo.imdb.entity.Actor;

import java.util.Optional;

public interface ActorRepo extends JpaRepository<Actor, Integer> {
    Optional<Actor> findByName(String name);
}
