package repository;


import controllers.TestApplication;
import models.MusicGenre;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenreRepositoryTest {

    @Test
    public void getAllGenres() {
        List<MusicGenre> genres = TestApplication.getGenreRepository().getAllGenres();
        assertEquals(4, genres.size());
        assertEquals("Rock", genres.get(0).getGenre());
    }

    @Test
    public void getArtistGenres() {
        List<MusicGenre> genres = TestApplication.getGenreRepository().getArtistGenres(1);
        assertEquals(2, genres.size());
        assertEquals("Reggae", genres.get(0).getGenre());
    }
}