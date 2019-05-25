Feature: Login
  As a user
  I want to log in
  So that I can access my profile

  Scenario: Log in an existing user
    Given John is at the login page
    When he fills in his email with "john@gmail.com"
    And he fills in his password with "password"
    And he presses Login
    Then the profile page should be shown

  Scenario: Log in with incorrect Password
    Given John is at the login page
    When he fills in his email with "john@gmail.com"
    And he fills in his password with "123"
    And he presses Login
    Then he is not redirected to the profile page

  Scenario: Log in with incorrect Email
    Given John is at the login page
    When he fills in his email with "johnfdsgf.doe@travelea.com"
    And he fills in his password with "password"
    And he presses Login
    Then he is not redirected to the profile page