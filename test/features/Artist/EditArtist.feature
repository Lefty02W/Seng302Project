Feature: Edit an artist page that I own
  As a user
  I want to edit an artist that I am an admin of

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: Edit an artist name
    Given User is logged in to the application
    When user is at their detailed artist page with id "7"
    And user changes artist name to "The Kings of the Amazon"
    And user changes members to "Buck, Canopy"
    And user changes biography to "Brand new band from the Amazon Rainforest"
    And user saves the edit of artist wih id "7"
    Then the artist changes are saved in the database

