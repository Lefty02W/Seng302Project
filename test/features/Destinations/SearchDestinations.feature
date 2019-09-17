Feature: Search for a destination
  As a user
  I want to search for a destination
  So I can find them quickly

  Scenario: Search for a public destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a public destination with name "Christchurch"
    Then the destination "Christchurch" is displayed in the search result

  Scenario: Search for a private destination
    Given User is logged in to the application
    And user is at the destinations page
    When uer searches for a private destination with name "Home"
    Then the destination "Home" is displayed in the search result

  Scenario: Search for a non-existent public destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a public destination with name "Thisisalongstringtotestthedestinationssearchfunctionality"
    Then the destination "Thisisalongstringtotestthedestinationssearchfunctionality" is not displayed in the search result
    And the search result is empty

  Scenario: Search for a non-existent private destination
    Given User is logged in to the application
    And user is at the destinations page
    When user searches for a private destination with name "Thisisanotherlongstringtotestthedestinationssearchfunctionality"
    Then the destination "Thisisanotherlongstringtotestthedestinationssearchfunctionality" is not displayed in the search result
    And the search result is empty


