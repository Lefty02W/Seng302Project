package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class holds the data for a destination
 */
public class Destination {

    private Integer id;
    private String name;
    private String type;
    private String district;
    private double latitude;
    private double longitude;
    private String country;

    public Destination(String name, String type, String district, double latitude, double longitude, String country) {
        this.name = name;
        this.type = type;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
    }

    public Destination() {

     }

    /**
     * Converting a Destination object into a JsonNode
     *
     * @param toConvert the destination to convert
     * @return the JsonNode created
     */
    public static JsonNode toJson(Destination toConvert) {
        JsonNode converted = Json.toJson(toConvert);
        return converted;
    }

    /**
     * Converting a passed JsonNode into a Destination object
     *
     * @param toConvert the JsonNode to convert
     * @return the converted Destination object
     */
    public static Destination fromJson(JsonNode toConvert) {
        Destination converted = Json.fromJson(toConvert, Destination.class);
        return converted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }



}