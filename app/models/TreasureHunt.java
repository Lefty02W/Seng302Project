package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Model class to store a Treasure Hunt
 */
@Entity
public class TreasureHunt extends Model {
    @Id
    @Constraints.Required
    private int treasureHuntId;

    @Constraints.Required
    private String riddle;

    @Constraints.Required
    private int profileId;

    @Constraints.Required
    private int destinationId;

    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private int startDate;

    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private int endDate;

    public static final Finder<String, TreasureHunt> find = new Finder<>(TreasureHunt.class);

    public TreasureHunt(int treasureHuntId, String riddle, int profileId, int destinationId, int startDate, int endDate) {
        this.treasureHuntId = treasureHuntId;
        this.riddle = riddle;
        this.profileId = profileId;
        this.destinationId = destinationId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getTreasureHuntId() {
        return treasureHuntId;
    }

    public String getRiddle() {
        return riddle;
    }

    public int getTreasureHuntDestinationId() {
        return destinationId;
    }

    public int getTreasureHuntProfileId() {
        return profileId;
    }

    public int getStartDate() {
        return startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }

    public void setTreasureHuntId(int treasureHuntId) {
        this.treasureHuntId = treasureHuntId;
    }

}
