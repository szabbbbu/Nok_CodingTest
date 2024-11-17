package robert.szabo.imdb.entity;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@IdClass(MovieID.class)
public class Movie {
    @Id
    @Column(name = "title")
    private String Title;
    @Id
    private Integer DirID;

    @ManyToOne
    @JoinColumn(name = "movie_dir", referencedColumnName = "dirid")
    private Director Director;

    @Column(name = "duration")
    private long Duration; // time in sec

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "acted_in_movie",
            joinColumns = {
                    @JoinColumn(name = "movie_title", referencedColumnName = "title"),
                    @JoinColumn(name = "movie_dir", referencedColumnName = "DirID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_actor", referencedColumnName = "ActorID")
            }
    )
    private List<Actor> Actors;

    public Movie() {
        Title = "aaa";
        this.DirID = 2;
        this.Director = new Director();
        this.Duration = 0;
    }

    public Movie(String title, Integer dirID, Director director, long duration, List<Actor> actors) {
        Title = title;
        DirID = dirID;
        Director = director;
        Duration = duration;
        Actors = actors;
    }

    public String getTitle() {
        return Title;
    }

    public Integer getDirID() {
        return DirID;
    }

    public robert.szabo.imdb.entity.Director getDirector() {
        return Director;
    }

    public long getDuration() {
        return Duration;
    }

    public List<Actor> getActors() {
        return Actors;
    }
}
