Feature: Edit a destination
  As a user
  I want to edit a destination
  So that I can then update my created destination

  Background:
    Given I am logged into the application as user "john@gmail.com" with password "password"


#  Scenario: Edit a destination
#    Given User is at the edit destinations page for destination "513"
#    Given user is at the destinations page
#    And the user has a destination with id "512"
#    When user changes Name to "Hello"
#    And user changes Type to "World"
#    And user changes Country to "New Zealand"
#    And user presses the Save button
#    Then user is redirected to the destination page
#    And the destination is displayed with the updated fields

#  Scenario:  Edit a destination with invalid latitude
#    Given User is at the edit destinations page
#    And the user has a destination with id "513"
#    When user changes the Latitude field with "-91"
#    And he presses the Save button
#    Then the user is not redirected to the destinations page
#    And the flashing error message is shown
#    And the destination is not updated
#
#  Scenario:  Edit a destination with invalid longitude
#    Given User is at the edit destinations page
#    And the user has a destination with id "513"
#    When user changes the Longitude field with "181"
#    And he presses the Save button
#    Then the user is not redirected to the destinations page
#    And the flashing error message is shown
#    And the destination is not updated
#
#  Scenario:  Edit a destination with no name
#    Given User is at the edit destinations page
#    And the user has a destination with id "513"
#    When user changes the Name field with ""
#    And he presses the Save button
#    Then the user is not redirected to the destinations page
#    And the destination is not updated
#
#  Scenario:  Edit a destination with no district
#    Given User is at the edit destinations page
#    And the user has a destination with id "513"
#    When user changes the District field with ""
#    And he presses the Save button
#    Then the Destination page should be shown
#    And the destination is displayed with the updated fields

  Scenario: Editing traveller types of a destination
    Given I am on the "/destinations/show/false" page
    When I press the edit button on destination "1"
    And I select "Backpacker" and "Thrillseeker" from the traveller type dropdown
    And I press the Save button to save the destination
    Then I am redirected to the destinations page
    And destination 1 now has traveller types; "Backpacker" and "Thrillseeker"

