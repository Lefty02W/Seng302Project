Feature: view destinations
  As a user
  I want to sign up to public destinations
  So that I can use them



  Scenario: sign up to destination
    Given User is logged in to the application
    And user is at the destinations page
    When User clicks on public destinations
    And Signs up to a public destination "Christchurch"
    And User clicks on private destinations
    Then Private destinations contains "Christchurch"