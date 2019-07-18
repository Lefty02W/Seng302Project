Feature: Delete personal photos
  As a user
  I want to be able to delete any of my personal photos
  So that I can keep my photos up to date

  Scenario: Delete a photo
    Given John is on his profile page
    When he presses the delete button on photo with id 1
    Then photo 1 is removed from the database