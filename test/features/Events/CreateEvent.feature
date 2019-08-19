Feature: Create an event
  As a user
  I want to create an event
  So I can plan trips around events

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Invalid start date
    Given I am on the events page
    And I put "Luke's Test Event" into the "eventName" form field
    And I put "Gig" into the "typeForm" form field
    And I put "16" into the "ageForm" form field
    And I put "1" into the "destinationId" form field
    And I put "1" into the "genreForm" form field
    And I put "1" into the "artistDropdown" form field
    And I put "Thu Nov 19 19:00:00 NZDT 2018" into the "startDate" form field
    And I put "Thu Nov 19 19:00:00 NZDT 2020" into the "endDate" form field
    And I submit the event form
    Then The user is redirected with an "error" flash

  Scenario: Invalid create endDate before startDate
    Given I am on the events page
    And I put "Luke's Test Event" into the "eventName" form field
    And I put "Gig" into the "typeForm" form field
    And I put "16" into the "ageForm" form field
    And I put "1" into the "destinationId" form field
    And I put "1" into the "genreForm" form field
    And I put "1" into the "artistDropdown" form field
    And I put "Thu Nov 19 19:12:00 NZDT 2020" into the "startDate" form field
    And I put "Thu Nov 19 19:01:00 NZDT 2020" into the "endDate" form field
    And I submit the event form
    Then The user is redirected with an "error" flash
