Feature: Sign Up
  As a user
  I want to sign up
  So that I can manage my travels
  Scenario: Sign up a new user
    Given John is at the sign up page
    When he fills in First Name with "John"
    And he fills in Middle Name with "Gherkin"
    And he fills in Last Name with "Doe"
    And he fills in Email with "john.gherkin.doe@travelea.com"
    And he fills in Password with "password"
    And he fills in Gender with "Male"
    And he fills in Birth date with "01/04/2019"
    And he fills in Nationalities with "New Zealand"
    And he fills in Passport with "New Zealand"
    And he selects "Holidaymaker" from Traveller Type
    And he presses OK
    Then the login page should be shown