package models;

import io.ebean.Model;
import play.data.format.Formats;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model class to store a Treasure Hunt
 */
@Entity
public class TreasureHunt extends Model {

    @Id
    private int treasureHuntId;

    private int profileId;

    private int destinationId;

    private String riddle;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date endDate;

    private int softDelete;

    private Destination destination;

    private static DateFormat dateFormatEntry = new SimpleDateFormat("yyyy-MM-dd");

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


    public void setDestinationIdString(String destId){
        this.destinationId = Integer.parseInt(destId);
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void setStartDateString(String date){
        try {
            this.startDate = dateFormatEntry.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setEndDateString(String date){
        try {
            this.endDate = dateFormatEntry.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get a formatted string of the end date
     * @return formatted date string
     */
    public String getEndDateString() {
        return dateFormatEntry.format(endDate);
    }

    /**
     * Method to get a formatted string of the start date
     * @return formatted date string
     */
    public String getStartDateString() {
        return dateFormatEntry.format(startDate);
    }

    public String getEntryDate(Date date) {
        return dateFormatEntry.format(date);
    }

    public void setSetSoftDelete(int setSoftDelete) { this.softDelete = setSoftDelete; }

}
