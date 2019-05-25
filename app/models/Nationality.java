package models;

import io.ebean.Finder;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Nationality {

    @Id
    @Constraints.Required
    private int nationality_id;
    @Constraints.Required
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
