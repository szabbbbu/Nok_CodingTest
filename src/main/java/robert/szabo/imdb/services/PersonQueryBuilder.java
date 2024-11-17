package robert.szabo.imdb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import robert.szabo.imdb.entity.Actor;
import robert.szabo.imdb.entity.Director;
import robert.szabo.imdb.entity.Movie;
import robert.szabo.imdb.repository.ActorRepo;
import robert.szabo.imdb.repository.DirectorRepo;
import robert.szabo.imdb.repository.MovieRepo;

import java.util.List;
import java.util.Optional;

@Repository
public class PersonQueryBuilder {
    @Autowired
    private ActorRepo actorRepo;
    @Autowired
    private DirectorRepo dirRepo;
    @Autowired
    private MovieRepo movieRepo;

    public void createNewPerson(String name, String nat, String role) {

        switch (role.toLowerCase()) {
            case "director":
//                System.out.println("MAKE A DIRECTOR");
                dirRepo.save(new Director(name, nat));
                break;
            case "actor":
//                System.out.println("MAKE AN ACTOR");
                actorRepo.save(new Actor(name, nat));
                break;
            default:
                System.out.println("INVALID ROLE: " + "\"" + role + "\"");
                break;
        }
    }
    public boolean deletePerson(String pName) {
        try {
            Optional<Actor> findAnyActor = actorRepo.findByName(pName);
            if (findAnyActor.isPresent()) {
                Actor a = findAnyActor.get();
                System.out.printf("Actor with name %s found. Deleting...\n", pName);
                List<Movie> actorMovies = a.getMovies();
                System.out.println("!!!" +actorMovies.size());
                for (Movie m : actorMovies) {
//                    System.out.println("DELETING ACTOR FROM MOVIE: " + m.getTitle());
                    m.getActors().remove(a);
                    movieRepo.save(m);
                }
                actorRepo.delete(a);
            }
            else {
                System.out.printf("Could not find an actor with name: %s.\n", pName);
            }

            Optional<Director> findAnyDir = dirRepo.findByName(pName);
            if (findAnyDir.isPresent()) {
                System.out.printf("Director found with name: %s Deleting...\n", pName);
                dirRepo.delete(findAnyDir.get());
            }
            else {
                System.out.printf("Could not find a Director with this name: %s\n", pName);
                return false;
            }
            return true;
        } catch (Exception ex) {
//            System.out.println("EXCEPTION!");
//            ex.printStackTrace();
            //A director deletion was attempted while still attached to a movie
            if (ex instanceof DataIntegrityViolationException) {
                System.err.println("ERROR: Cannot delete this director, as he is still connected to Movies in the Database!");
            }
            return false;
        }
    }

    public Optional<Director> getDirector(String name) {
        Optional<Director> d = dirRepo.findByName(name);
        return d;
    }

    public Optional<Actor> getActor(String name) {
        Optional<Actor> a = actorRepo.findByName(name);
        return a;
    }


}


