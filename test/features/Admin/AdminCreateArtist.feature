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

