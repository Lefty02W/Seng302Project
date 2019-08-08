Feature: Create an artist page
  As a user
  I want to create an artist
  So that I can then view my created artist page

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

