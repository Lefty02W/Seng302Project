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
    And he presses save
    Then he is redirected to the create destination page and destination is not saved

  Scenario: Create a public destination which is the same as another users private destination
    Given User is logged in to the application
    And user is at the destinations page
    And Steve Miller has a private destination with name "Waiau", type "town", and country "New Zealand"
    When user clicks on the add new destination button
    And user creates a public destination with name "Waiau", type "town", and country NewZealand
    Then user with id "2" private destination with name "Waiau", type "town", and country "New Zealand" doesnt exist
    And user with id "2" is following the new public destination with name "Waiau", type "town", and country "New Zealand"

  Scenario: 2 users have the same private destinations, one user makes their destination public
    Given User is logged into the application
    And user is at the destinations page
    And user with id "1" has a private destination with name "UC", type "uni", and country "New Zealand"
    And user with id "2" has a private destination with name "UC", type "uni", and country "New Zealand"
    When user with id "1" updates his private destination with name "UC", type "uni", and country "New Zealand" to be public
    Then user with id "2" private destination with name "UC", type "uni", and country "New Zealand" doesnt exist
    And user with id "2" is following the new public destination with name "UC", type "uni", and country "New Zealand"