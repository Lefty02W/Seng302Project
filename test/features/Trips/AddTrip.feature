Feature: Create a trip
  As a user
  I want to be able to create a trip
  So that I can view my trips
  Scenario: Create a trip
    Given user is at trips page
    When user clicks on the add new trip button
    #And user selects a destination
    #And user presses add destination
    #And user selects another destination
    #And user presses add destination
    #And user enters a trip name
    #And user presses Save Trip
    #Then the trip page loads displaying his trips
    #And trip is saved in the database

  Scenario: No destination selected
    Given user is at trips page
    When user clicks on the add new trip button
    And user presses add destination without selecting a destination
    Then destination is not added
    And stay on create trips page