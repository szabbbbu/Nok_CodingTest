package robert.szabo.imdb.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ActorID;
    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "Actors", fetch = FetchType.EAGER)
    private List<Movie> Movies;

    @Column(nullable = false)
    private String Nat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNat() {
        return Nat;
    }

    public void setNat(String nat) {
        Nat = nat;
    }

    public List<Movie> getMovies() {
        return this.Movies;
    }

    public Actor() {
        this.name = "Robert";
        this.Nat = "USA";
    }
    public Actor(String name, String nat) {
        this.name = name;
        this.Nat = nat;
    }

}
