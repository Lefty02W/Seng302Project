package repository;


import org.junit.Test;
import models.Artist;

public class ArtistRepositoryTest {

    @Test
    public void insertArtist(){
        Artist artist = new Artist();
        artist.setArtistName("LukeUnitTestArtist");
        artist.setBiography("Description for my artist");


    }
}
