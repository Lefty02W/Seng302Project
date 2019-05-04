# Created by ambroseledbrook at 3/04/19
Feature: Edit User Profile
  As a user
  I want to be able to edit my user profile
  So that my details are always up to date

 # Scenario: Edit profile modal is opened on click of Edit Profile
   # Given I am logged in
   # And I am on the profile page
   # When I press the edit profile button
   # Then The edit profile dialog is shown

  Scenario: I can perform an edit of my profile
    Given I am logged into the application
    And I am on the edit profile page
    When I change my first name to "Jenny"
    And I change my traveller types to "Backpacker, Thrillseeker"
    And I change my middle name to "Max"
    And I press the Save button
    Then I am redirected to my profile page
    And My new profile data is saved

   Scenario: I cannot save my profile with no traveller types
     Given I am logged into the application
     And I am on the edit profile page
     When I change my traveller types to ""
     And I try to save the edit
     Then I am not redirected to the profile page
     And my edit is not saved
