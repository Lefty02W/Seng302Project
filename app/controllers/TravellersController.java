package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * This class is the controller for the travellers.scala.html file, it provides the route to the
 * travellers page
 */
public class TravellersController extends Controller {


    /**
     * This method shows the travellers page on the screen
     * @return
     */
    public Result show() {
        /**
        ArrayList<User> users = new ArrayList<>();
        String[] nationalities = {"New Zealander", "European"};
        String[] passports = {"New Zealand", "United Kingdom", "New Zealand", "United Kingdom"};
        String[] types = {"Backpacker", "Thrill Seeker"};
        User user1 = new User("John", "Jefferson","Cook", new Date(), nationalities, passports, types);
        User user2 = new User("Steve", "James","Smith", new Date(), nationalities, passports, types);
        User user3 = new User("Eric", "Ben","Cook", new Date(), nationalities, passports, types);
        User user4 = new User("Merry", "Jefferson","Shankland", new Date(), nationalities, passports, types);
        User user5 = new User("Jess", "beans","Williams", new Date(), nationalities, passports, types);
        User user6 = new User("Larry", "Jefferson","Jones", new Date(), nationalities, passports, types);
        User user7 = new User("Bill", "Jeff","Jefferson", new Date(), nationalities, passports, types);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        users.add(user7);*/
        return ok(travellers.render(readUsers()));
    }


    /**
     * This methods reads users in from a json file and returns them as a list
     * @return an ArrayList of users
     */
    private ArrayList<User> readUsers() {
        ArrayList<User> userList = new ArrayList<>();
        // Reading the users in from the json file
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("seng302_profile.json"));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);

            for (int i = 0; i < rootNode.size(); i++) {
                userList.add(User.fromJson(rootNode.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
