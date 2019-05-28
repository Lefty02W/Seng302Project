Feature: Create a destination from admin page
  As an admin
  I want to be able to create users destinations
  So I can add new destinations for the users to use

  Scenario: Admin creates a public destination for their user
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin fills the create destination form with correct data
    And selects them self as the profile
    Then the admin presses save
    And the new destination is added to the admins 2 destinations


  Scenario: Admin creates a public destination for another user
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin fills the form with correct data including name as "adminsTest"
    And selects user 1 as the profile
    Then the admin presses save
    And the new destination is added to user 1 destinations