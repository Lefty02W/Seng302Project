Feature: Admin Create Treasure Hunt
  As a admin
  I want to be able to create treasure hunts


  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Admin creates valid treasure hunt
    Given admin is on the admin page
    When Press the create treasure hunt button
    And I enter "Another riddle" as the "riddle"
    And I enter "2001-12-12" as the "startDate"
    And I enter "2002-12-12" as the "endDate"
    And I enter "1" as the "destinationId"
    And I enter "3" as the "profileId"
    When I save the treasure hunt
    Then I should be redirected back to the admin page
    And The treasure hunt is saved to the database
