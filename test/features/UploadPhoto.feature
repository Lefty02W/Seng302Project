Feature: Uploading photo
  As a user
  I want to be able to upload a personal photo
  So that I can keeps photos on my profile

  Background:
        Given I am logged into the application as user "john@gmail.com" with password "password"

  Scenario: I can upload a photo
        Given I press the Add Photo button
        Then I choose a file to upload
        And I try to submit the file
        Then I am redirected to the profile page
        And a message should be shown telling me the photo upload is successful
