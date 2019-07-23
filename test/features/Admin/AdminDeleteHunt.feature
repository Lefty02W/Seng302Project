Feature: Admin Delete Treasure Hunt
  As a admin
  I want to be able to delete treasure hunts


  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Admin deletes treasure hunt
    Given admin is on the admin page
    When Press the delete button on treasure hunt "4"
    Then I am be redirected back to the admin page
    And The treasure hunt is deleted from the database