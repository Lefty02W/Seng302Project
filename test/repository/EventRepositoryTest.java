package repository;

import controllers.TestApplication;
import models.Events;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EventRepositoryTest {

    private final int ARTIST_WITH_EVENTS = 1;
    private final int ARTIST_WITHOUT_EVENTS = 2;

    @Test
    public void getArtistEventsArtistHasEvents() {
        List<Events> events = TestApplication.getEventRepository().getArtistEvents(ARTIST_WITH_EVENTS);
        assertEquals(2, events.size());
    }

    @Test
    public void getArtistEventsArtistHasNoEvents() {
        List<Events> events = TestApplication.getEventRepository().getArtistEvents(ARTIST_WITHOUT_EVENTS);
        assertTrue(events.isEmpty());
    }

}