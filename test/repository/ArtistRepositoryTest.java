package repository;

import models.Artist;
import org.junit.Test;

public class ArtistRepositoryTest  {

    @Test
    public void insertArtist(){
        Artist artist = new Artist();
        artist.setArtistName("LukeUnitTestArtist");
        artist.setBiography("Description for my artist");


    }
}
