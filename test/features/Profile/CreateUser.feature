Feature: Create a user profile
  As a user
  I want to be able to create a new user profile
  So that I can access the full website


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

  Scenario: Sign up a new user backend
    Given John is at the sign up page
    When he enters the First Name "John"
    And he enters the Middle Name "Gherkin"
    And he enters the Last Name "Doe"
    And he enters the Email "john.gherkin.doe@travelea.com"
    And he enters the Password "password"
    And he enters the Gender "Male"
    And he enters the Birth date "01/04/2019"
    And he enters the Nationalities "New Zealand, China"
    And he enters the Passport "New Zealand, China"
    And he chooses "Holidaymaker, Thrillseeker" in Traveller Type
    And he submits
    Then his account should be saved


  Scenario: Create valid user profile
    Given I am on the landing page
    When I press the create user button
    And I enter "James" into the "firstName" field
    And I enter "Johnston" into the "lastName" field
    And I enter "james@johnston.com" into the "email" field
    And I enter "password" into the "password" field
    And I enter "1969-05-05" into the "birthDate" field
    And I enter "Male" into the "gender" field
    And I enter "New Zealand", "Europe" into the "nationalitiesForm" field
    And I enter "Backpacker" into the "travellerTypesForm" field
    And I enter "Belarus", "Argentina" into the "passportsForm" field
    Then I save my new profile
    And My user profile is saved in the database
    And my passports are "New Zealand,Europe"