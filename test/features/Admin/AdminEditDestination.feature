Feature: Edit a destination from admin page
  As an admin
  I want to be able to enter all users destinations
  So I can keep all destination consistent and up to date

  Background:
    Given Password hash setup has been done


  Scenario: Admin edits a destination
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on destination 2
    And changes the latitude to "12.2"
    And sets the name to "Haere Roa"
    And selects the the save button
    Then admin is redirected to the admin page with a valid notification
    Then destinations latitude is updated in the database
    And destination name is updated in the database

  Scenario: Admin edits a destination with invalid longitude
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on destination 2
    And changes the longitude to "200"
    And selects the the save button
    Then admin is redirected to the admin page with an invalid notification
    And destinations latitude is not updated in the database

  Scenario: Admin edits a destination by changing visibility
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on destination 2
    And changes the visibility to 1
    And selects the the save button
    Then admin is redirected to the admin page with a valid notification
    And destinations visibility is updated in the database
