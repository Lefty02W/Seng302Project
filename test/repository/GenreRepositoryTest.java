package repository;

import controllers.ProvideApplication;
import models.MusicGenre;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenreRepositoryTest extends ProvideApplication {

    @Test
    public void getAllGenres() {
        injectRepositories();
        List<MusicGenre> genres = genreRepository.getAllGenres();
        assertEquals(4, genres.size());
        assertEquals("Rock", genres.get(0).getGenre());
    }

    @Test
    public void getArtistGenres() {
        injectRepositories();
        List<MusicGenre> genres = genreRepository.getArtistGenres(1);
        assertEquals(2, genres.size());
        assertEquals("Reggae", genres.get(0).getGenre());
    }
}