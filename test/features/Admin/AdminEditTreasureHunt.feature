Feature: Edit a treasure hunt from admin page
  As an admin
  I want to be able to edit all users treasure hunts
  So I can keep all treasure hunts consistent and up to date

  Scenario: Admin edits a valid treasure hunt with riddle
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on one of the treasure hunts
    And admin selects edit on treasure hunt 2
    And changes the riddle to "A concrete jungle"
    And selects the the save treasure hunt button
    Then I am redirected to the admin page
    Then The admins riddle is updated in the database

  Scenario: Admin edits a valid treasure hunt with start time
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on one of the treasure hunts
    And admin selects edit on treasure hunt 2
    And changes the start date to "2019-01-04"
    And selects the the save treasure hunt button
    Then I am redirected to the admin page
    Then The admins start date is updated in the database

  Scenario: Admin edits a valid treasure hunt with end time
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on one of the treasure hunts
    And admin selects edit on treasure hunt 2
    And changes the end date to "2020-01-01"
    And selects the the save treasure hunt button
    Then I am redirected to the admin page
    Then The admins end date is updated in the database

  Scenario: Admin edits an invalid treasure hunt with end date after start date
    Given Admin is logged in to the application
    And admin is on the admin page
    When admin presses edit on one of the treasure hunts
    And admin selects edit on treasure hunt 2
    And changes the start date to "2020-01-01"
    And changes the end date to "2016-01-04"
    And selects the the save treasure hunt button
    Then I am redirected to the admin page with an invalid notification
    Then The edit is not updated in the database
