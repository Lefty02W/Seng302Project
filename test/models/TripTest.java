package models;

import org.junit.Assert;
import org.junit.Before;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.fail;

public class TripTest {

    private Trip trip;
    private Trip tripNullDates;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Before
    public void setUp() throws Exception {
        TripDestination tripDest1 = new TripDestination(1, dateFormat.parse("04/05/2018"), dateFormat.parse("14/05/2018"), 1);
        TripDestination tripDest2 = new TripDestination(2, dateFormat.parse("14/05/2018"), dateFormat.parse("20/05/2018"), 2);
        TripDestination tripDest3 = new TripDestination(3, dateFormat.parse("20/05/2018"), dateFormat.parse("27/05/2018"), 3);
        ArrayList<TripDestination> dests = new ArrayList<>();
        dests.add(tripDest1);
        dests.add(tripDest2);
        dests.add(tripDest3);
        trip = new Trip(dests, "Trip to Nelson");

        TripDestination tripDest4 = new TripDestination(1, null, null, 1);
        TripDestination tripDest5 = new TripDestination(2, null, null, 2);
        TripDestination tripDest6 = new TripDestination(3, null, null, 3);
        ArrayList<TripDestination> dests1 = new ArrayList<>();
        dests1.add(tripDest4);
        dests1.add(tripDest5);
        dests1.add(tripDest6);
        tripNullDates = new Trip(dests1, "Trip to USA");
    }

    /**
     * Testing that the travel time of a trip is calculated correctly when dates are given
     */
    //@Test
    public void getTravelTime() {
        long travelTime = trip.getTravelTime();
        Assert.assertEquals(23, travelTime);
    }

    /**
     * Testing that null date values are handled when trying to calculate the travel time of a trip
     */
    //@Test
    public void getTravelTimeNull() {
        long travelTime = tripNullDates.getTravelTime();
        Assert.assertEquals(0, travelTime);
    }

    /**
     * Testing that the start date string of a trip is formatted correctly
     */
    //@Test
    public void getStartDateString() {
        String startDateString = trip.getStartDateString();
        try {
            Date startDate = new SimpleDateFormat("dd-MMM-yyyy").parse(startDateString);
            Assert.assertNotNull(startDate);
        } catch (ParseException e) {
            fail();
        }
    }

    /**
     * Testing that getting the start date for a null date is handled correctly
     */
    //@Test
    public void getStartDateStringNull() {
        String startDateString = tripNullDates.getStartDateString();
        Assert.assertEquals("", startDateString);
    }

}