Feature: Edit Treasure Hunt
  As a user
  I want to edit my Treasure Hunts

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: Edit the destination of treasure hunt
    Given I am on the treasure hunts page
    When I press edit on one of my treasure hunts
    And I select destination 2 from the dropdown
    When I press the save button to save the treasure hunt
    Then I am redirected to the treasure hunts page
    And The edit is saved to the database


  Scenario: Valid edit of the end date of a treasure hunt
    Given I am on the treasure hunts page
    When I press edit on one of my treasure hunts
    And I set the end date to "2020-12-30"
    When I press the save button to save the treasure hunt
    Then I am redirected to the treasure hunts page
    And The end date is updated in the database


  Scenario: Edit the riddle of a treasure hunt
    Given I am on the treasure hunts page
    When I press edit on one of my treasure hunts
    And I set the end date to "A new riddle"
    When I press the save button to save the treasure hunt
    Then I am redirected to the treasure hunts page
    And The riddle is updated in the database


  Scenario: Valid edit of the start date of a treasure hunt
    Given I am on the treasure hunts page
    When I press edit on one of my treasure hunts
    And I set the start date to "2000-12-30"
    When I press the save button to save the treasure hunt
    Then I am redirected to the treasure hunts page
    And The start date is updated in the database


  Scenario: Editing end date to be before start date is caught as invalid
    Given I am on the treasure hunts page
    When I press edit on one of my treasure hunts
    And I set the end date to "2000-12-30"
    And I set the start date to "2020-12-30"
    When I press the save button to save the treasure hunt
    Then I am redirected to the treasure hunts page
    And The edit is not saved to the database



