Feature: Edit Treasure Hunt
  As a user
  I want to edit my Treasure Hunts

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: Change destination of treasure hunt
    Given I am on the treasure hunts page
    When I press edit on one of my treasure hunts
    And I select destination 2 from the dropdown
    When I press the save button to save the treasure hunt
    Then I am redirected to the treasure hunts page
    And The edit is saved to the database


