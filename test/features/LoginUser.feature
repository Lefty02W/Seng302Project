Feature: Login
  As a user
  I want to log in
  So that I can access my profile
  Scenario: Log in an existing user
    Given John is at the sign up page
    When he fills in his email with "john.gherkin.doe@travelea.com"
    And he types "password" for his password
    And he presses OK
    Then the login page should be shown