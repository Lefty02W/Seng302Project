package models;


import javax.persistence.Entity;


/**
 * Modal class to hold a link from an artist to a genre
 */
@Entity
public class ArtistGenre {


    private int artistId;
    private int genreId;

    public ArtistGenre(int artistId, int genreId) {
        this.artistId = artistId;
        this.genreId = genreId;
    }
}
