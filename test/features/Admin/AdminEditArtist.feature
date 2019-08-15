Feature: Edit an artist profile as an admin
  As an admin user
  I want to be able to edit any artists in the system

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Edit artists genres
    Given I am on the admin page
    And I select artist 5 to edit
    And I change the "genreForm" to "1" and "2"
    And I save the edit of artist "5"
    Then The new genres are saved

  Scenario: Edit artists countries
    Given I am on the admin page
    And I select artist 5 to edit
    And I change the "countries" to "New Zealand" and "Fiji"
    And I save the edit of artist "5"
    Then The new countries are saved

  Scenario: Edit artists admins
    Given I am on the admin page
    And I select artist 5 to edit
    And I change the "adminForm" to "1" and "2"
    And I save the edit of artist "5"
    Then The new admins are saved

  Scenario: Edit artists members
    Given I am on the admin page
    And I select artist 5 to edit
    And I change the "members" to "James Johnston" and "Steve Stevenson"
    And I save the edit of artist "5"
    Then The new members are saved
