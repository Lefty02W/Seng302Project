package repository;

import controllers.TestApplication;
import models.Artist;
import org.junit.Test;

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
        assertEquals(10, artists.size());
    }

    @Test
    public void searchArtistsEmptySearchFullResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("","","", 0, 0, 1);
        assertEquals(10, artists.size());
    }

    @Test
    public void searchArtistsOneResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Mr Walsh","","", 0, 0, 1);
        assertEquals(1, artists.size());
    }

    @Test
    public void searchArtistsMultipleResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("","Indie","", 0, 0, 1);
        assertEquals(3, artists.size());
    }

    @Test
    public void searchArtistsNoneResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Bob","","", 0, 0, 1);
        assertEquals(0, artists.size());
    }

    @Test
    public void searchValidArtistsNonFollowedNoneResultFollowed(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Mr Walsh","","", 1, 0, 1);
        assertEquals(0, artists.size());
    }

    @Test
    public void searchValidArtistsFollowedOneResultFollowed(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Jerry","","", 1, 0, 1);
        assertEquals(1, artists.size());
    }

    @Test
    public void searchHalfNameValidArtistsFollowedOneResultFollowed(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Je","","", 1, 0, 1);
        assertEquals(1, artists.size());
    }

    @Test
    public void searchHalfNameValidArtistsOneResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Mr","","", 0, 0, 1);
        assertEquals(1, artists.size());
    }

    @Test
    public void searchAllFieldsOneResult(){
        List<Artist> artists = TestApplication.getArtistRepository().searchArtist("Je","Indie","New Zealand", 1, 0, 1);
        assertEquals(1, artists.size());
    }
}
