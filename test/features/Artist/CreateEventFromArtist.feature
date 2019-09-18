#Feature: Create an event from viewArtist
#  As an artist
#  I want to be able to create an event from my artist profile
#
#  Background:
#    Given I am logged into the application as user "replace this" with password "replace this"
#
#  Scenario: Successful create event from artist page redirects to artist page
#    Given I am on my view artists page for artist "replace this"
#    And I enter "replace this" into the "eventName" field for the CreateEventForm
#    And I enter "replace this" into the "typeForm" field for the CreateEventForm
#    And I enter "replace this" into the "ageForm" field for the CreateEventForm
#    And I enter "replace this" into the "destinationId" field for the CreateEventForm
#    And I enter "replace this" into the "genreForm" field for the CreateEventForm
#    And I enter "replace this" into the "artistDropDown" field for the CreateEventForm
#    And I enter "replace this" into the "startDate" field for the CreateEventForm
#    And I enter "replace this" into the "endDate" field for the CreateEventForm
#    And I enter "replace this" into the "description" field for the CreateEventForm
#    And I submit the Create Event Form
#    Then I am redirected back to my artists page with "replace this" as the url
#    And an "info" flash is displayed
