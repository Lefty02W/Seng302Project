Feature: Create an artist page
  As a user
  I want to create an artist
  So that I can then view my created artist page

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Remove artist profile picture
    Given I am on the detailed view for artist 1
    When I press the button to remove the artists profile picture
    Then I am redirected to the detailed view for artist 1
    And Artists 1 profile picture has been removed

