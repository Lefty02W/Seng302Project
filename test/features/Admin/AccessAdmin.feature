Feature: Access admin page
  As a user
  I want to access the admin page

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Remove artist profile picture
    Given I am on the detailed view for artist 1
    When I press the button to remove the artists profile picture
    Then I am redirected to the detailed view for artist 1
    And Artists 1 profile picture has been removed

  Scenario: Access the admin page as a non admin
    Given I am logged into the application as a non admin
    When he fills "/admin/admins/0" into the URL
    And he tries to access the admin page
    Then the admin page should not be shown and the profile page should be shown

  Scenario: Access the admin page as an admin
    Given I am logged into the application as an admin
    When he fills "/admin/admins/0" into the URL
    And the admin tries to access the admin page
    Then the admin page should be shown


  Scenario: The make admin endpoint cannot be accessed unless an admin is logged in
    Given I am logged into the application as a non admin
    When I enter the following url "/admin/1/admin"
    Then User 1 is not made an admin
    And There should be a flashing present saying "You do not have permission to do this"
