package repository;


import controllers.TestApplication;
import models.MusicGenre;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class GenreRepositoryTest {

    @Test
    public void getArtistGenres() {
        Optional<List<MusicGenre>> genres = TestApplication.getGenreRepository().getArtistGenres(1);
        if (genres.isPresent()) {
            assertEquals(2, genres.get().size());
            assertEquals("Indie", genres.get().get(0).getGenre());
        }
    }
}