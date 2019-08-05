package models;


import javax.persistence.Entity;


/**
 * Modal class to hold a link from an artist to a genre
 */
@Entity
public class ArtistGenre {


    private int artist;
    private int genre;

    public ArtistGenre(int artist, int genre) {
        this.artist = artist;
        this.genre = genre;
    }
}
