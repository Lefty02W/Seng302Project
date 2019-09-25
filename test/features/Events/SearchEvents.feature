Feature: Search for an event
  As a user
  I want to search events
  And be able to filter them by if I am attending or not

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

    Scenario: Filter search attending events
      Given I am on the events page
      And I select attending from the advance search field
      And I submit the search form
      Then a event is displayed that I am attending


