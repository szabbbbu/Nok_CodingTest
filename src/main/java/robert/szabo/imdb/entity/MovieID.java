package robert.szabo.imdb.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class MovieID implements Serializable {
    private String Title;
    private Integer DirID;

    public MovieID() {
        this.Title = "XXXX";
        this.DirID= 0;
    }


    public MovieID(String title, int dirID) {
        this.Title = title;
        this.DirID = dirID;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getDirID() {
        return this.DirID;
    }

    public void setDirID(int dirID) {
        this.DirID = dirID;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (this == obj) return true;

        MovieID id = (MovieID) obj;
        return Objects.equals(Title, id.Title) &&
                Objects.equals(this.DirID, id.DirID);
    }
    @Override
    public int hashCode(){
        return Objects.hash(this.Title, this.DirID);
    }
}
