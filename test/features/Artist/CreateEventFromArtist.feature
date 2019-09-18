Feature: Create an event from viewArtist
  As an artist
  I want to be able to create an event from my artist profile

  Background:
    Given I am logged into the application as user "hasAnArtist@gmail.com" with password "password"

  Scenario: Successful create event from artist page redirects to artist page
    Given I am on my view artists page for artist "9"
    And I enter "Daddy sloths in town" into the "eventName" field for the CreateEventForm
    And I enter "Gig" into the "typeForm" field for the CreateEventForm
    And I enter "16" into the "ageForm" field for the CreateEventForm
    And I enter "1" into the "destinationId" field for the CreateEventForm
    And I enter "1" into the "genreForm" field for the CreateEventForm
    And I enter "9" into the "artistDropdown" field for the CreateEventForm
    And I enter "2022-08-22T19:00" into the "startDate" field for the CreateEventForm
    And I enter "2022-08-22T21:00" into the "endDate" field for the CreateEventForm
    And I enter "This is an event testing artists get redirected" into the "description" field for the CreateEventForm
    And I submit the Create Event Form
    Then I am redirected back to my artists page with "/artists/9/events/0" as the url