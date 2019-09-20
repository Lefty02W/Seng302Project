Feature: Create an artist page
  As a user
  I want to create an artist
  So that I can then view my created artist page

  Background:
    Given I am logged into the application as user "bob@gmail.com" with password "password"

  Scenario: Create an artist
    Given User is logged in to the application
    When user is at the artist page
    And user enters "King Crimson" for artist name
    And user enters "Progressive Rock" for artist genres
    And user enters "Robert Frip" for artist members
    And user enters "Some dudes, man" for artist bio
    And user enters "United States of America" for artist country
    And user presses save artist
    Then the artist is saved in the database

  Scenario: Create an artist with invalid links and ensure it won't save.
    Given User is logged in to the application
    When user is at the artist page
    And user enters "Autechre" for artist name
    And user enters "IDM/Experimental" for artist genres
    And user enters "Rob Brown, Sean Booth" for artist members
    And user enters "Autechre are an English electronic music duo consisting of Rob Brown and Sean Booth, both from Rochdale, Greater Manchester. " for artist bio
    And user enters "United Kingdom of Great Britain and Northern Ireland" for artist country
    And user changes facebook link to "https://www.google.com/"
    And user changes instagram link to "https://www.google.com/"
    And user changes spotify link to "https://www.google.com/"
    And user changes twitter link to "https://www.google.com/"
    And user changes website link to "https://www.amazon.com/kingsofamazon"
    And user presses save artist
    Then the artist is not saved in the database


  Scenario: Artist genre links are saved after create
    Given I am on the artist create page
    And I enter "Jim James" into the "artistName" form field
    And I enter "Indie" and "Rock" into the "genreFrom" form field
    And I enter "James, Steve" into the "members" form field
    And I enter "3 times as good as 2 Chainz" into the "biography" form field
    And I enter "United States of America" into the "countries" form field
    And I submit the form
    Then The artist genre links are saved

  Scenario: Duplicate artist name is caught
    Given I am on the artist create page
    And I enter "James" into the "artistName" form field
    And I enter "Rock" and "Indie" into the "genreFrom" form field
    And I enter "James, Steve" into the "members" form field
    And I enter "3 times as good as 2 Chainz" into the "biography" form field
    And I enter "United States of America" into the "countries" form field
    And I enter "2" into the "adminForm" field
    And I submit the form
    Then There is a flashing sent with id "error"

