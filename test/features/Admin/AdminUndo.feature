Feature: Undo Delete
  As an admin
  I want to be able to undo deletes I have made on the current screen


  Background:
    Given I am logged into the application as an admin


  Scenario: Admin deletes a profile and reverts the delete
    Given the admin is on the admin page
    And there is a profile with id 9
    And the admin deletes the profile with id 9
    When the admin presses the undo button
    Then the profile 9 is restored
    And the profile 9 is no longer in the delete stack

  Scenario: Admin deletes a treasure hunt and reverts it
    Given the admin is on the admin page
    And there is a treasure hunt with id 5
    And the admin deletes the treasure hunt 5
    When the admin presses the undo button
    Then the treasure hut 5 is restored
    And the treasure hunt is removed from the delete stack

  Scenario: Admin deletes a destination and then deletes a trip and then reverts the destination delete
    Given the admin is on the admin page
    And user 3 has a destination with id 5
    And there is a trip with id 2
    Then the admin deletes the trip 2
    And the admin deletes the destination 5
    When the admin presses the undo button
    Then the destination 5 is restored
    And trip 2 is still soft deleted

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

  Scenario: Deleting a profile adds it to the undo stack
    Given the admin is on the admin page
    And the admin deletes the profile with id 10
    Then a flashing is shown confirming the delete
    And the profile is added to the undo stack

