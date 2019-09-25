Feature: Attend an event
  As a user
  I want to attend an event
  So I others can know I am going.

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Attend event I am not attending
    Given I am on the event page
    And I attend an event I am not currently attending
    Then I am redirected with an "info" flash

  Scenario: Leave event I am attending
    Given I am on the event page
    And I leave an event I am currently attending
    Then I am redirected with an "info" flash

