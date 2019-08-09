
Feature: Admin handling artist creation requests
  As an admin
  I want to be able to accept or reject artist creation request
  To allow me to ensure only valid artists are in the system

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Newly created artist is shown on admin page
    Given A regular user creates a new artist with name "LilBible"
    And An admin navigates to the artists requests table on the admin page
    Then The new artist "LilBible" is in the table
