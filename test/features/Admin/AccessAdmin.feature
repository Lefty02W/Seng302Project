Feature: Access admin page
  As a user
  I want to access the admin page
  Scenario: Access the admin page as a non admin
    Given I am logged into the application as a non admin
    When he fills "/admin" into the URL
    And he tries to access the admin page
    Then the admin page should not be shown and the profile page should be shown

  Scenario: Access the admin page as an admin
    Given I am logged into the application as an admin
    When he fills "/admin" into the URL
    And the admin tries to access the admin page
    Then the admin page should be shown
