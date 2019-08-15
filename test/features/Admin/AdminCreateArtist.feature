Feature: Admin create artist page
  As an admin
  I am able to create artist pages

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Admin creates an artist
    Given admin is on the admin page
    When admin presses the create artist button
    And admin enters "Green Day" for artist name
    And admin enters "Billy Joe" for artist members
    And admin enters the user with email "bob@gmail.com" for artist admins
    And admin enters "The best rock band" for artist bio
    And admin presses save artist
    Then the admin artist is saved in the database

#  Scenario: Artist genre links are saved after create
#    Given I am on the artist create page
#    And I enter "Jim James" into the "artistName" form field
#    And I enter "Indie" and "Rock" into the "genreFrom" form field
#    And I enter "James, Steve" into the "members" form field
#    And I enter "3 times as good as 2 Chainz" into the "biography" form field
#    And I enter "United States of America" into the "countries" form field
#    And I submit the form
#    Then The artist genre links are saved
#
#  Scenario: Duplicate artist name is caught
#    Given I am on the artist create page
#    And I enter "James" into the "artistName" form field
#    And I enter "Rock" and "Indie" into the "genreFrom" form field
#    And I enter "James, Steve" into the "members" form field
#    And I enter "3 times as good as 2 Chainz" into the "biography" form field
#    And I enter "United States of America" into the "countries" form field
#    And I enter "2" into the "adminForm" field
#    And I submit the form
#    Then There is a flashing sent with id "error"