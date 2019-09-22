package repository;

import controllers.TestApplication;
import models.Events;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EventRepositoryTest {

    private final int ARTIST_WITH_EVENTS = 1;
    private final int ARTIST_WITHOUT_EVENTS = 4;
    private final int PROFILE_WITH_MORE_THAN_TEN = 2;
    private final int PROFILE_WITH_LESS_THAN_TEN = 13;
    private final int PROFILE_WITH_EXPIRED = 14;

    @Test
    public void getArtistEventsArtistHasEvents() {
        List<Events> events = TestApplication.getEventRepository().getArtistEventsPage(ARTIST_WITH_EVENTS, 0);
        assertEquals(6, events.size());
    }

    @Test
    public void getArtistEventsArtistHasNoEvents() {
        List<Events> events = TestApplication.getEventRepository().getArtistEventsPage(ARTIST_WITHOUT_EVENTS, 0);
        assertTrue(events.isEmpty());
    }

    @Test
    public void getNextTenUpComingEventsUserHasMoreThanTen() {
        assertEquals(10, TestApplication.getEventRepository().getNextTenUpComingEvents(PROFILE_WITH_MORE_THAN_TEN).size());
    }

    @Test
    public void getNextTenUpComingEventsUserHasMoreLessTen() {
        assertEquals(4, TestApplication.getEventRepository().getNextTenUpComingEvents(PROFILE_WITH_LESS_THAN_TEN).size());

    }

    @Test
    public void getNextTenUpComingEventsUserHasExpiredEvents() {
        assertEquals(1, TestApplication.getEventRepository().getNextTenUpComingEvents(PROFILE_WITH_EXPIRED).size());

    }

}