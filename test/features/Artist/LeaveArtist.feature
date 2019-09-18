Feature: Create an artist page
  As a user
  I want to create an artist
  So that I can then view my created artist page

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: I leave an artist I am in
    Given I am on artist 6 page
    And I am an admin of artist 6
    When I press the leave artist button
    Then I am redirected to the artists page
    And I am no longer an admin of artist 6
