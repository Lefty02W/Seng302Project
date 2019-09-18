Feature: Create an event from viewArtist
  As an artist
  I want to be able to create an event from my artist profile

  Background:
    Given I am logged into the application as user "hasAnArtist@gmail.com" with password "password"

  Scenario: Successful create event from artist page redirects to artist page
    Given I am on my view artists page for artist "8"
    And I enter "replace this" into the "Daddy sloths in town" field for the CreateEventForm
    And I enter "replace this" into the "Gig" field for the CreateEventForm
    And I enter "replace this" into the "21" field for the CreateEventForm
    And I enter "replace this" into the "1" field for the CreateEventForm
    And I enter "replace this" into the "1" field for the CreateEventForm
    And I enter "replace this" into the "8" field for the CreateEventForm
    And I enter "replace this" into the "2022-08-22T19:00" field for the CreateEventForm
    And I enter "replace this" into the "2022-08-22T21:00" field for the CreateEventForm
    And I enter "replace this" into the "This is an event testing artists get redirected" field for the CreateEventForm
    And I submit the Create Event Form
    Then I am redirected back to my artists page with "artists/8/events/0" as the url
    And an "info" flash is displayed
