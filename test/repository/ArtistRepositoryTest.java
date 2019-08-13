package repository;

import controllers.TestApplication;
import models.Artist;
import org.junit.Test;
import org.junit.runner.notification.RunListener;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArtistRepositoryTest  {

    @Test
    public void insertArtist(){
        Artist artist = new Artist();
        artist.setArtistName("LukeUnitTestArtist");
        artist.setBiography("Description for my artist");


    }

    @Test
    public void getAllArtist(){
        List<Artist> artists = TestApplication.getArtistRepository().getAllArtists();
        assertEquals(3, artists.size());
    }

}
