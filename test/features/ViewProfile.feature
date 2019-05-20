Feature: View Profile

  Scenario: See a greeting on the profile page
    Given John has logged in with email "admin@admin.com" and password "admin"
    Then he should see a greeting "Welcome, admin!"

  Scenario: See a default profile photo
    Given John has logged in with email "admin@admin.com" and password "admin"
    And he has no profile photo
    Then he should see a default profile photo

  Scenario: View name in personal information
    Given admin has logged in with email "admin@admin.com" and password "admin"
    Then he should see a first name "admin"
    And a middle name: "John"
    And a last name: "Smith"