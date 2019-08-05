Feature: Create an artist page
  As a user
  I want to create an artist
  So that I can then view my created artist page

  Scenario: Create an artist
    Given User is logged in to the application
    And user is at the artist page
    When user clicks on the add new artist button
    And user enters "" for artist name
    And user enters "" for artist genres
    And user enters "" for artist members
    And user enters "" for artist bio
    And user enters "" for artist country
    And user presses save artist
    Then the artist is saved in the database

