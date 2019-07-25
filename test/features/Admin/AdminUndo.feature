Feature: Undo Delete
  As an admin
  I want to be able to undo deletes I have made on the current screen
  Scenario: Admin deletes a profile and reverts the delete
    Given the admin is on the admin page
    And the there is a profile with id "69"
    And the admin deletes the profile with id "69"
    When the admin selects the change on the undo dropdown
    And the admin presses the undo button
    Then the profile "id" is restored
    And the profile "id" is no longer in the delete stack

  Scenario: Admin deletes a treasure hun and reverts it
    Given the admin is on the admin page
    And there is a treasure hunt with id
    And the admin deletes the treasure hunt
    When the admin selects the treasure hunt delete on the dropdown
    And the admin presses the undo button
    Then the treasure hut is restored
    And the treasure hunt is removed from the delete stack


