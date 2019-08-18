Feature: Create an artist page
  As a user
  I want to be able to follow and un follow an artist
  So that I can then save it as a followed artist

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Follow an artist
    Given User is logged in to the application
    When user is at the artist page
    And user selects an artist with id "3"
    And user presses follow artist
    Then link is updated flashing is shown


  Scenario: Un follow an artist
    Given User is logged in to the application
    When user is at the artist page
    And user selects an artist with id "2"
    And the user has followed this artist
    And user presses un follow artist
    Then link is updated flashing is shown

