package robert.szabo.imdb.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import robert.szabo.imdb.entity.Actor;
import robert.szabo.imdb.entity.Movie;
import robert.szabo.imdb.entity.MovieID;
import robert.szabo.imdb.repository.MovieRepo;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MovieQueryBuilder {
    private String Title;
    private String DirRegex;
    private String ActorRegex;
    private boolean ascByLen;
    private boolean descByLen;
    private char MainCmd;


    @Autowired
    public MovieRepo repo;
    @PersistenceContext
    private EntityManager entityManager;

    public MovieQueryBuilder() {
        ascByLen = false;
        descByLen = false;
    }
    // BUILDER FUNCS
    public void withTitle(String title) {
        this.Title = title;
    }
    public void withDirector(String dir) {
        this.DirRegex = dir;
    }
    public void withActor(String actor) {
        this.ActorRegex = actor;
    }
    public void withAsc() {
        ascByLen = true;
    }
    public void withDesc() {
        descByLen = true;
    }

    public void setMainCmd(char cmd) {
        this.MainCmd = cmd;
    }


    public boolean handleCreateMovie(Movie newMovie) {
        if (repo.existsById(new MovieID(newMovie.getTitle(), newMovie.getDirID()))) {
            System.err.println("WARNING: Movie with this title and director already exists. Overwriting...");
        }
        try {
            repo.save(newMovie);
            return true;
        } catch (Exception ex) {
            System.out.println("ERROR: Could not save the movie to DB");
            return false;
        }
    }

    public List<Movie> buildMovieSelection() {
        if (entityManager == null) throw new IllegalStateException("Entity manager cant be null");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> movieRoot = cq.from(Movie.class);

        ArrayList<Predicate> predicates = new ArrayList<>();

        if (Title != null && !Title.isEmpty()) {
            Predicate titlePred = cb.like(movieRoot.get(("Title")), "%"+Title+"%");
            predicates.add(titlePred);
        }
        if (DirRegex != null && !DirRegex.isEmpty()) {
            Predicate dirPred = cb.like(movieRoot.get("Director").get("Name"), "%" + this.DirRegex + "%");
            predicates.add(dirPred);
        }

        if (ActorRegex != null && !ActorRegex.isEmpty()) {
//            System.out.println(this.ActorRegex);
            Join<Movie, Actor> actorJoin = movieRoot.join("Actors");
//            System.out.println(actorJoin);
            Predicate ap = cb.like(actorJoin.get("Name"), "%" + this.ActorRegex + "%");
            predicates.add(ap);
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));


        if (ascByLen && descByLen) {
            System.out.println("WARNING: You chose '-la' and '-ld' in the same command. Defaulting to '-la'");
        }
        if (ascByLen) {
            cq.orderBy(cb.asc(movieRoot.get("Duration")));
        } else if (descByLen) {
            cq.orderBy(cb.desc(movieRoot.get("Duration")));
        }
        else {
            cq.orderBy(cb.asc(movieRoot.get("Duration")));
        }

        return entityManager.createQuery(cq).getResultList();
    }
}
