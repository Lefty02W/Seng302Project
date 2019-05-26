Feature: Edit a destination from admin page
  As an admin
  I want to be able to enter all users destinations
  So I can keep all destination consistent and up to date

  Scenario: Admin edits a destination
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin selects edit on destination 2
    And changes the latitude to 12.39
    And sets the name to "yeetVil"
    And selects the the save button
    Then destination 2 latitude is updated to 12.39
    And destination 2 name is updated to "yeetVil"
