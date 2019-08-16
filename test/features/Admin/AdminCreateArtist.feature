Feature: Admin create artist page
  As an admin
  I am able to create artist pages

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Admin creates an artist
    Given admin is on the admin page
    When admin presses the create artist button
    And admin enters "Green Day" for artist name
    And admin enters "Billy Joe" for artist members
    And admin enters the user with email "bob@gmail.com" for artist admins
    And admin enters "The best rock band" for artist bio
    And admin presses save artist
    Then the admin artist is saved in the database

  Scenario: Artist genre links are saved after create
    Given admin is on the admin page
    When admin presses the create artist button
    And admin enters "George's Story" for artist name
    And admin enters "George, Jacoco, Selenium" for artist members
    And admin enters the user with email "bob@gmail.com" for artist admins
    And admin enters "The band of the year" for artist bio
    And admin enters "1,2" for artist genres
    And admin presses save artist
    Then The artist genre links are saved

  Scenario: Artist country links are saved after create
    Given admin is on the admin page
    When admin presses the create artist button
    And admin enters "Dusk Winds" for artist name
    And admin enters "Bill, Bob" for artist members
    And admin enters the user with email "bob@gmail.com" for artist admins
    And admin enters "Hardcore Death Metal Band from Picton" for artist bio
    And admin enters "New Zealand,Papua New Guinea" for artist country
    And admin presses save artist
    Then The artist country links are saved

  Scenario: Artist admin links are saved after create
    Given admin is on the admin page
    When admin presses the create artist button
    And admin enters "Cherry Pop" for artist name
    And admin enters "John, Bob" for artist members
    And admin enters "Aesthetic Indie band from Japan" for artist bio
    And admin enters "1,2" for artist admin
    And admin presses save artist
    Then The artist admin links are saved