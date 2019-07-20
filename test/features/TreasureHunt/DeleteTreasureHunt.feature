Feature: Delete Treasure Hunt
  As a user
  I want to be able to delete my Treasure Hunts

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: Delete on of my treasure hunts
    Given I am on the treasure hunts page
    When I press delete on treasure hunt 1
    Then The treasure hunt is removed from the database