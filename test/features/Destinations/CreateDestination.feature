Feature: Create a destination
  As a user
  I want to create a destination
  So that I can then view my created destination
  Scenario: Create a destination
    Given User is logged in to the application
    And user is at the destinations page
    When user clicks on the add new destination button
    And he fills in Name with "Port Moresby"
    And he fills in Type with "Village"
    And he fills in Country with "Papua New Guinea"
    And he presses Destination Save
    Then he is redirected to the destinations page

  Scenario:  Create a destination invalid longitude
    Given user is at the destinations page
    When user clicks on the add new destination button
    And he fills in Name with "Christchurch"
    And he fills in Type with "City"
    And he fills in Country with "New Zealand"
    And he fills in Longitude as "200"
    And he presses Destination Save
    Then the Destination page should be shown

  Scenario:  Create a destination with a traveller type
    Given user is at the destinations page
    When user clicks on the add new destination button
    And he fills in Name with "Erskine"
    And he fills in Type with "Building"
    And he fills in Country with "New Zealand"
    And he selects "ThrillSeeker" as the traveller type
    And he presses Destination Save
    Then he is redirected to the destinations page