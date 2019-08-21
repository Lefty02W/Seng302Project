package models;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * model class to hold information on music genres
 */
@Entity
public class MusicGenre {

    @Id
    private int genreId;
    private String genre;

    public int getGenreId() {
        return genreId;
    }

    public String getGenre() {
        return genre;
    }
}
