package robert.szabo.imdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import robert.szabo.imdb.entity.Director;

import java.util.Optional;

public interface DirectorRepo extends JpaRepository<Director, Integer> {
    Optional<Director> findByName(String name);
}
