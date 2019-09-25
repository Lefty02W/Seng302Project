Feature: Public Destinations
  As a user
  I want to create and private destinations
  So that I can have the option to follow public destinations


  Scenario: Create a private destination same as a public destination
    Given User is logged in to the application
    And user is at the destinations page
    When user clicks on the add new destination button
    And he fills in name with "Matakana"
    And he fills in type with "Town"
    And he fills in country with "New Zealand"
    And he does not fill out a traveller type
    And he presses save
    Then he is redirected to the create destination page and destination is not saved
