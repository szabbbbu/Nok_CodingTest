package robert.szabo.imdb.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import robert.szabo.imdb.entity.Movie;
import robert.szabo.imdb.entity.MovieID;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface MovieRepo extends JpaRepository<Movie, MovieID> {

}
