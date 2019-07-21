Feature: Create Treasure Hunt
  As a user
  I want to create a Treasure Hunt

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: Create valid treasure hunt
    Given I am on the treasure hunts page
    When I insert a riddle "The cake is a lie"
    And I select the destination "Tokyo"
    And I enter today's date for start date
    And I enter tomorrow's date for end date
    When I click create treasure hunt
    Then I should be shown confirmation that the treasure hunt was made


  Scenario: Create valid treasure hunt
    Given I am on the treasure hunts page
    When I insert a riddle "The cake is a lie"
    And I select the destination "Tokyo"
    And I enter tomorrow's date for end date
    And I enter today's date for start date
    When I click create treasure hunt
    Then I should be shown a message to fix the dates