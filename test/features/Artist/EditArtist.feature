Feature: Edit an artist page that I own
  As a user
  I want to edit an artist that I am an admin of

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: Edit an artists basic info
    Given User is logged in to the application
    When user is at their detailed artist page with id "7"
    And user changes artist name to "The Kings of the Amazon"
    And user changes members to "Buck, Canopy"
    And user changes biography to "Brand new band from the Amazon Rainforest"
    And user saves the edit of artist wih id "7"
    Then the artist changes are saved in the database

  Scenario: Edit an artists external links
    Given User is logged in to the application
    When user is at their detailed artist page with id "7"
    And user changes facebook link to "https://www.facebook.com/kingsofamazon"
    And user changes instagram link to "https://www.instagram.com/kingsofamazon"
    And user changes spotify link to "https://www.spotify.com/kingsofamazon"
    And user changes twitter link to "https://www.twitter.com/kingsofamazon"
    And user changes website link to "https://www.amazon.com/kingsofamazon"
    And user saves the edit of artist wih id "7"
    Then the artist link changes are saved in the database

  Scenario: Edit an artists country
    Given User is logged in to the application
    When user is at their detailed artist page with id "7"
    And user changes country to "Mexico" and "Peru"
    And user saves the edit of artist wih id "7"
    Then the artist country changes are saved in the database

  Scenario: Edit an artists genre
    Given User is logged in to the application
    When user is at their detailed artist page with id "7"
    And user changes genre to "1" and "3"
    And user saves the edit of artist wih id "7"
    Then the artist genre changes are saved in the database

  Scenario: Edit an artists admins
    Given User is logged in to the application
    When user is at their detailed artist page with id "7"
    And user changes admins to "2"
    And user saves the edit of artist wih id "7"
    Then the artist admin changes are saved in the database