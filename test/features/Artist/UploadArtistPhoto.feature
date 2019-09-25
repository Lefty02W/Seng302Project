Feature: Upload Artist Photo
  As an admin of an artist,
  I want to be able to upload a profile photo for my artist

  Scenario: Upload a profile photo to an owned artist
    Given User is logged in to the application
    And user is at their detailed artist page with id "7"
    When the user uploads a photo for artist with id "7"
    Then the artist with id "7" has that photo as their profile photo

  Scenario: Upload a profile photo to an artist with an existing one
    Given User is logged in to the application
    And user is at their detailed artist page with id "1"
    And the artist with id "1" has an existing photo as their profile photo
    When the user uploads a photo for artist with id "1"
    Then the artist with id "1" has that photo as their profile photo