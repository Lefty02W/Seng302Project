package repository;

import controllers.TestApplication;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class AttendEventRepositoryTest {

    private final int EVENT_WITH_ATTENDEES = 12;
    private final List<Integer> ATTENDEES = new ArrayList<>(Arrays.asList(2, 13, 14));
    private final int EVENT_WITH_NO_ATTENDEES = 14;

    @Test
    public void getAttendingUsersNoUsers() {
        assertEquals(ATTENDEES, TestApplication.getAttendEventRepository().getAttendingUsers(EVENT_WITH_ATTENDEES));
    }

    @Test
    public void getAttendingUsersHasUsers() {
    }

}