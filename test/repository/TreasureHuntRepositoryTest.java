package repository;

import controllers.ProvideApplication;
import models.TreasureHunt;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreasureHuntRepositoryTest extends ProvideApplication {


    @Ignore
    @Test
    public void getAllTreasureHunts() {
        injectRepositories();
        List<TreasureHunt> hunts = treasureHuntRepository.getAllTreasureHunts();

        assertEquals(3, hunts.size());
        assertEquals("Yes but No", hunts.get(0).getRiddle());
    }

    @Test
    public void getAllUserTreasureHunts() {
        injectRepositories();
        List<TreasureHunt> hunts = treasureHuntRepository.getAllUserTreasureHunts(1);

        assertEquals(1, hunts.size());
        assertEquals("A riddle", hunts.get(0).getRiddle());
    }
}