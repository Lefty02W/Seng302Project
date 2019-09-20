Feature: Search for a destination
  As a user
  I want to search for a destination
  So I can find them quickly

  Scenario: Search for a public destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a public destination with name "New York"
    Then the public destination "New York" is displayed in the search result

  Scenario: Search for a private destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a private destination with name "Tokyo"
    Then the private destination "Tokyo" is displayed in the search result

  Scenario: Search for a non-existent public destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a public destination with name "Thisisalongstringtotestthedestinationssearchfunctionality"
    Then the private destination "Thisisalongstringtotestthedestinationssearchfunctionality" is not displayed in the search result

  Scenario: Search for a non-existent private destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a private destination with name "Thisisanotherlongstringtotestthedestinationssearchfunctionality"
    Then the public destination "Thisisanotherlongstringtotestthedestinationssearchfunctionality" is not displayed in the search result

  Scenario: Empty search
    Given User is logged in to the application
    And user is at the destinations page
    When user submits empty search
    Then an error message should be shown telling the user to enter a name


