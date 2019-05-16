package models;

import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PassportCountry {

    @Id
    private int passport_country_id;
    private String passport_name;

    public static final Finder<String, PassportCountry> find = new Finder<>(PassportCountry.class);


    public PassportCountry(int passport_country_id, String passport_name) {
        this.passport_country_id = passport_country_id;
        this.passport_name = passport_name;
    }

    public PassportCountry(String passport_name) {
        this.passport_name = passport_name;
    }

    public int getPassportId() {
        return passport_country_id;
    }

    public String getPassportName() {
        return passport_name;
    }
}