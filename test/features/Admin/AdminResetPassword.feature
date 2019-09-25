Feature: Reset Password
  As an admin
  I want to be able to reset the password of any user in the application

  Background:
    Given I am logged into the application as an admin

  Scenario: Admin resets the password of a user
    Given the admin is on the admin page
    When admin selects change password for user with id "15"
    And the admin chooses new password to be "neWPa55WorD"
    And the admin clicks save
    Then user 15 password will now equal "neWPa55WorD"