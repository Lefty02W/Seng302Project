Feature: Delete an event
  As a user
  I want to delete an event

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Delete event
    Given I am on the events tab for my artist 2
    When I select event 4 to delete
    Then I am redirected to the events tab for artist 2
    And Event 4 has been deleted
