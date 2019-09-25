Feature: Create a trip
  As a user
  I want to be able to create a trip
  So that I can view my trips


  Scenario: No destination selected
    Given user is at trips page
    When user clicks on the add new trip button
    And user presses add destination without selecting a destination
    Then destination is not added