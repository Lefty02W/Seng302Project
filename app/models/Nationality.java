package models;

import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Nationality {

    @Id
    private int nationality_id;
    private String nationality_name;

    public static final Finder<String, Nationality> find = new Finder<>(Nationality.class);


    public Nationality(int nationality_id, String nationality_name) {
        this.nationality_id = nationality_id;
        this.nationality_name = nationality_name;
    }

    public Nationality(String nationality_name) {
        this.nationality_name = nationality_name;
    }

    public int getNationalityId() {
        return nationality_id;
    }

    public String getNationalityName() {
        return nationality_name;
    }
}
