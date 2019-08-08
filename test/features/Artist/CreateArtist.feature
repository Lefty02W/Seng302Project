Feature: Create an artist page
  As a user
  I want to create an artist
  So that I can then view my created artist page

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Create an artist
    Given User is logged in to the application
    When user is at the artist page
    And user enters "King Crimson" for artist name
    And user enters "Progressive Rock" for artist genres
    And user enters "Robert Frip" for artist members
    And user enters "Some dudes, man" for artist bio
    And user enters "United States of America" for artist country
    And user presses save artist
    Then the artist is saved in the database


#  Scenario: All selected profiles are link when creating an artist
#
#  Scenario: All selected genres are link when creating an artist
#
#  Scenario: All selected countries are link when creating an artist

  Scenario: Duplicate artist name is caught
    Given I am on the artist create page
    And I enter "6 Chainz" into the "name" form field
    And I enter "Rock" and "Indie" into the "genreFrom" form field
    And I enter "James, Steve" into the "members" form field
    And I enter "3 times as good as 2 Chainz" into the "biography" form field
    And I enter "United States of America" into the "countries" form field
    And I submit the form
    Then There is a flashing sent with id "error"

