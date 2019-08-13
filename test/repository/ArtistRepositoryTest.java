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
        assertEquals(4, artists.size());
    }

    @Test
    public void searchArtistsEmptyResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("","","", 1);
        assertEquals(4, artists.size());
    }

    @Test
    public void searchArtistsOneResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("James","","", 1);
        assertEquals(1, artists.size());
    }

    @Test
    public void searchArtistsMultipleResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("","Indie","", 1);
        assertEquals(2, artists.size());
    }

    @Test
    public void searchArtistsNoneResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Bob","","", 1);
        assertEquals(0, artists.size());
    }

}
