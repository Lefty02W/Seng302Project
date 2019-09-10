Feature: Delete a destination
  As a user
  I want to delete a destination

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Cannot delete destination that is in an event
    Given I am on the destinations page
    When I select destination 9 to delete
    Then A flashing is shown
    And Destination 9 has not been deleted

  Scenario: Cannot delete destination that is in a trip
    Given I am on the destinations page
    When I select destination 10 to delete
    Then A flashing is shown
    And Destination 10 has not been deleted

  Scenario: Cannot delete destination that is in a treasure hunt
    Given I am on the destinations page
    When I select destination 11 to delete
    Then A flashing is shown
    And Destination 11 has not been deleted

  Scenario: Delete a destination
    Given I am on the destinations page
    When I select destination 12 to delete
    Then A flashing is shown
    And Destination 12 has been deleted
