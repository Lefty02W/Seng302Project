package models;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Finder;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This class holds a profile personal data
 */
@Entity
public class User {

    //Set in create user
    public String first_name;
    public String middle_name;
    public String last_name;
    public String email;
    public String password;
    public Date birth_date;
    public ArrayList<String> nationalities;
    public ArrayList<String> passport_countries;

    public static Finder<Integer, User> find = new Finder<>(User.class);


    private ArrayList<String> traveller_types;
    private String gender;


    // The below date formats are used to handle different thigns that need a specific date format to work
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
    private static SimpleDateFormat dateFormatSort = new SimpleDateFormat("dd/MM/YYYY");
    private static SimpleDateFormat dateFormatEntry = new SimpleDateFormat("YYYY-MM-dd");

    // The below attributes are used in json parsing only
    private String date_of_birth;
    private String nationality;
    private String passport_country;
    private String traveller_type;
    


    public User(String first_name, String middle_name, String last_name, String email, String password, String birth_date, String nationality, String passport_country, String traveller_type) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.middle_name = middle_name;
        this.email = email;
        this.password = password;
        try {
            this.birth_date = dateFormatSort.parse(birth_date);
            this.date_of_birth = dateFormatEntry.format(this.birth_date);
        } catch (ParseException e){
        }
        this.nationality = nationality;
        this.passport_country = passport_country;
        this.traveller_type = traveller_type;

    }


    /**
     * Constructor for a user
     * @param first_name the first name
     * @param middle_name the middle name
     * @param last_name the last name
     * @param date_of_birth the date of birth
     * @param nationality the nationality
     * @param passport_country the passport
     */
    public User(String first_name, String middle_name, String last_name, String date_of_birth, String nationality, String passport_country, String traveller_type) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.date_of_birth = date_of_birth;
        this.nationality = nationality;
        this.passport_country = passport_country;
        this.traveller_type = traveller_type;

    }

    /**
     * THis method parses a JsonNode into a User object
     * @param toConvert the JsonNode to parse
     * @return the parsed user object
     */
    public static User fromJson(JsonNode toConvert) {
        User converted = Json.fromJson(toConvert, User.class);
        return formatFromJson(converted);
    }

    /**
     * This method converts the strings parsed for nationalities, passports and types into lists
     * @param user The parsed User
     * @return the formatted User
     */
    private static User formatFromJson(User user) {
        //user.nationalities = user.nationality.split(",");
        //user.traveller_types = user.traveller_type.split(",");
        //user.passport_countries = user.passport_country.split(",");
        try {
            // This needs to be changed to work better
            user.birth_date = dateFormat.parse(user.date_of_birth);
            user.date_of_birth = dateFormatSort.format(user.birth_date);
            user.birth_date = dateFormatSort.parse(user.date_of_birth);
            user.date_of_birth = dateFormatEntry.format(user.birth_date);
        } catch (ParseException e){
            //#TODO Add something to throw an error message from here
        }
        return user;

    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPassport_country() {
        return passport_country;
    }

    public void setPassport_country(String passport_country) {
        this.passport_country = passport_country;
    }

    public String getTraveller_type() {
        return traveller_type;
    }

    public void setTraveller_type(String traveller_type) {
        this.traveller_type = traveller_type;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getNationalities() {
        return nationalities;
    }

    public void setNationalities(ArrayList<String> nationalities) {
        this.nationalities = nationalities;
    }

    public ArrayList<String> getPassport_countries() {
        return passport_countries;
    }

    public void setPassport_countries(ArrayList<String> passport_countries) {
        this.passport_countries = passport_countries;
    }

    public ArrayList<String> getTraveller_types() {
        return traveller_types;
    }

    public void setTraveller_types(ArrayList<String> traveller_types) {
        this.traveller_types = traveller_types;
    }
}
