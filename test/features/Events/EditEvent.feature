Feature: Create an event
  As a user
  I want to create an event
  So I can plan trips around events

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Edit event
    Given I am on the events page
    When I select event 5 to edit
    And I change event field "eventName" to "Mono"
    And I change event field "destinationId" to "13"
    And I change event field "ageForm" to "16"
    And I change event field "genreForm" to "2,3"
    And I save the edit of event 5
    Then I am redirected to the events page
    And The new event data is saved
