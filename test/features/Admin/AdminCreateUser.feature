Feature: Admin create a user profile
  As a admin
  I want to be able to create a new user profile

  Scenario: Create valid user profile on admin page
    Given I am logged into the application as an admin
    And I am on the admin page
    When I press the create user button
    And I enter "Sam" into the "firstName" admin field
    And I enter "Samson" into the "lastName" admin field
    And I enter "Sam@samson.com" into the "email" admin field
    And I enter "password" into the "password" admin field
    And I enter "1982-05-05" into the "birthDate" admin field
    And I enter "Male" into the "gender" admin field
    And I enter "New Zealand", "China" into the "nationalitiesForm" admin field
    And I enter "Backpacker" into the "travellerTypesForm" admin field
    And I enter "Belarus", "Argentina" into the "passportsForm" admin field
    Then admin saves the profile
    And The created profile is saved in the database
    And my passports are "Belarus, Argentina" or "Argentina, Belarus"
    And my nationalities are "New Zealand,China"