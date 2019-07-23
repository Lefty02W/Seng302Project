Feature: Create a Change Request
  As a user
  I want to create a Traveller type change request on a public destination I don't own
  So I can suggest edits to the admin's
  Scenario: Create a request with remove and add changes
    Given A logged in user is on the destinations page
    And there is a public destination with id "5"
    When the user fills the request form for the destination to remove traveller type "Groupie"
    And adds traveller type "Backpacker"
    Then the user presses the submit button
    And the requests pass to the admin

  Scenario: Create an add only traveller type request
    Given A logged in user is on the destinations page
    And there is a public destination with id "5"
    When the user fills in the request form with add "Gap year"
    Then the user submits the second request
    And the requests pass to the admin
#
#  Scenario: Create a remove only traveller type request
#    Given A logged in user is on the destinations page
#    And there is a public destination with traveller type "Groupie"
#    When the user fills in the request form with remove "Groupie"
#    Then the user is redirected to the destinations page
#    And the requests pass to the admin
