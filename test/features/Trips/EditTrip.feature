Feature: Edit a Trip
  As a user
  I want to be able to edit any of my existing trips
  So that I can keep them up to date

  Scenario: I cannot edit a trip to have only one destination
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I remove all but one destinations from the trip
#    And I try to save the trip
#    Then I am redirected back to the edit trip page
#    And A error message is shown saying "A trip must have at least two destinations"

  Scenario: I cannot a reorder destination to be next to itself while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I select the first destination to edit
#    And I move the first destination to be last
#    Then I am redirected back to the edit trip page
#    And A error message is shown saying "The same destination cannot be after itself in a trip"

  Scenario: I cannot a change a destination to be next to itself while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And Select the first destination to edit
#    And I change it to be the same as the second destination
#    Then I am redirected back to the edit trip page
#    And A error message is shown saying "The same destination cannot be after itself in a trip"

  Scenario: I cannot add a destination to be next to itself while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I select the last destination to add
#    And I try to add the destination
#    Then I am redirected back to the edit trip page
#    And A error message is shown saying "The same destination cannot be after itself in a trip"

  Scenario: I cannot add a departure time that is before the arrival time while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I select a destination
#    And I enter "01/04/2001 12:30" into the arrival time
#    And I enter "01/04/1999 12:30" into the departure time
#    And I try to add the destination
#    Then I am redirected back to the edit trip page
#    And A error message is shown saying "The arrival date must be before the departure date"

  Scenario: I can reorder destinations while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 2" to edit
#    And I select the first destination to edit
#    And I change its order to 2
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint

  Scenario: I can change one of the destinations while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 3" to edit
#    And I select the first destination to edit
#    And I change the destination to one that isn't the second destination
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint

  Scenario: I can add a destination while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 4" to edit
#    And I select a destination that isn't the last destination
#    And I try to add the destination
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint

  Scenario: I can remove a destination while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 5" to edit
#    And I delete the last destination
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint

  Scenario: I can edit the arrival time value of a destination while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I select the first destination to edit
#    And I change the arrival time to "01/04/1994 11:30"
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint

  Scenario: I can edit the departure time value of a destination while editing a trip
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I select the first destination to edit
#    And I change the departure time to "01/04/2020 11:30"
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint

  Scenario: I can change the name of a trip while editing it
#    Given I am logged into the application as user "ralph@z.com" with password "password"
#    And I select my trip named "Trip 1" to edit
#    And I change the name to "Trip to Paris"
#    And I try to save the trip edit
#    Then I am redirected to the "/trips" endpoint
