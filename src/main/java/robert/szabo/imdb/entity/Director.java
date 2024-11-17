package robert.szabo.imdb.entity;
import jakarta.persistence.*;

@Entity
public class Director {
    @Id
    @Column(name = "dirid", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer DirId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String Nat;

    public Director() {
        this.name = "FF Coppolla";
        this.Nat = "USA";
    }

    public Director(String name, String nat) {
        this.name = name;
        Nat = nat;
    }

    public Integer getDirId() {
        return DirId;
    }

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



}
