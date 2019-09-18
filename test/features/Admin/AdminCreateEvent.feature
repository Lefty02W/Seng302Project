Feature: Admin create event page
  As an admin
  I am able to create events for artists

  Background:
    Given I am logged into the application as user "<string>" with password "<string>"

  Scenario: Admin creates a valid event
    Given admin is on the admin page
    And artist "2" is verified
    When admin enters "Cucumber Test Event" for event name
    And admin selects "Gig" for event type
    And admin selects "16" for age restriction
    And admin selects destination 1 for event destination
    And admin selects "1" for event genre
    And admin selects artist 1 for event artist
    And admin enters "2021-08-22T19:00" for event start date
    And admin enters "2021-08-22T21:00" for event end date
    And admin enter "this is an event made using a cucumber test" for event description
    Then the admin presses the save event
    And the admin is redirected to "/admin/events/0"
    And the event has been successfully created
