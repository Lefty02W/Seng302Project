Feature: Delete an event
  As an admin
  I want to be able to delete events

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Delete an event from admin page
    Given I am on the admin page
    When I delete event 2
    Then Event 2 is marked as soft deleted