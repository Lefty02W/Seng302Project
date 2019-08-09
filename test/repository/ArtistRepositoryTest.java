package repository;

import controllers.ProvideApplication;
import org.junit.Test;
import models.Artist;

public class ArtistRepositoryTest extends ProvideApplication {

    @Test
    public void insertArtist(){
        injectRepositories();
        Artist artist = new Artist();
        artist.setArtistName("LukeUnitTestArtist");
        artist.setBiography("Description for my artist");


    }
}
