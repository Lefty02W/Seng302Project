package repository;

import controllers.TestApplication;
import models.TreasureHunt;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreasureHuntRepositoryTest {

    @Ignore
    @Test
    public void getAllTreasureHunts() {
        List<TreasureHunt> hunts = TestApplication.getApplication().injector().instanceOf(TreasureHuntRepository.class).getAllTreasureHunts();

        assertEquals(3, hunts.size());
        assertEquals("Yes but No", hunts.get(0).getRiddle());
    }

    @Test
    public void getAllUserTreasureHunts() {
        List<TreasureHunt> hunts = TestApplication.getApplication().injector().instanceOf(TreasureHuntRepository.class).getAllUserTreasureHunts(1);

        assertEquals(2, hunts.size());
        assertEquals("A new riddle", hunts.get(0).getRiddle());
    }
}