package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Model class to store a Treasure Hunt
 */
@Entity
public class TreasureHunt extends Model {

    @Id
    private int treasureHuntId;

    private int profileId;

    @Constraints.Required
    private int destinationId;

    @Constraints.Required
    private String riddle;

    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date endDate;

    public static final Finder<String, TreasureHunt> find = new Finder<>(TreasureHunt.class);

    public TreasureHunt(String riddle, int destinationId, Date startDate, Date endDate) {
        this.riddle = riddle;
        this.destinationId = destinationId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getTreasureHuntId() {
        return treasureHuntId;
    }

    public int getTreasureHuntProfileId() {
        return profileId;
    }

    public String getRiddle() {
        return riddle;
    }

    public int getTreasureHuntDestinationId() {
        return destinationId;
    }

    public int getTreasureHuntUserId() {
        return profileId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }

    public void setTreasureHuntId(int treasureHuntId) {
        this.treasureHuntId = treasureHuntId;
    }

    public void setTreasureHuntProfileId(int profileId) {
        this.profileId = profileId;
    }


}
