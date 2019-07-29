Feature: Undo Delete
  As an admin
  I want to be able to undo deletes I have made on the current screen


  Background:
    Given I am logged into the application as an admin


  Scenario: Admin deletes a profile and reverts the delete
    Given the admin is on the admin page
    And there is a profile with id "9"
    And the admin deletes the profile with id "9"
    When the admin presses the undo button
    Then the profile "9" is restored
    And the profile "9" is no longer in the delete stack

  Scenario: Admin deletes a treasure hun and reverts it
    Given the admin is on the admin page
    And there is a treasure hunt with id
    And the admin deletes the treasure hunt
    When the admin selects the treasure hunt on the undo dropdown
    And the admin presses the undo button
    Then the treasure hut is restored
    And the treasure hunt is removed from the delete stack

  Scenario: Admin deletes a destination and then deletes a trip and the reverts the destination delete
    Given the admin is on the admin page
    And there is a destination with id
    And there is a trip with id
    Then the admin deletes the destination
    And the admin deletes the trip
    When the admin selects the destination on the undo dropdown
    And the admin presses the undo button
    Then the destination is restored
    And the treasure hunt is removed from the delete stack
    And the trip is still on the delete stack

  Scenario: Outdated command is removed and executed from undo stack
    Given the admin is on the admin page
    And command stack item 1 is more than one day old
    When the admin leaves the admin page
    Then command 1 should no longer be in the database
    And related destination 5 should be removed from the database

  Scenario: Deleting a treasure hunt adds it to the undo stack
    Given the admin is on the admin page
    And the admin deletes treasure hunt 5
    Then a flashing is shown confirming the delete
    And the treasure hunt is added to the undo stack


