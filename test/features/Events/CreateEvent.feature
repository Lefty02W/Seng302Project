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
    And I put "2018-08-22T19:00" into the "startDate" form field
    And I put "2020-08-22T19:00" into the "endDate" form field
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
    And I put "2020-08-22T21:00" into the "startDate" form field
    And I put "2020-08-22T19:00" into the "endDate" form field
    And I submit the event form
    Then The user is redirected with an "error" flash

  Scenario: Successful create event startDate before endDate
    Given I am on the events page
    And I put "Luke's Test Event" into the "eventName" form field
    And I put "Gig" into the "typeForm" form field
    And I put "16" into the "ageForm" form field
    And I put "1" into the "destinationId" form field
    And I put "1" into the "genreForm" form field
    And I put "1" into the "artistDropdown" form field
    And I put "2020-08-22T19:00" into the "startDate" form field
    And I put "2020-08-22T21:00" into the "endDate" form field
    And I submit the event form
    Then The user is redirected with an "info" flash
