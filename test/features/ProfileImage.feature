Feature: Changing profile image

  Background:
    Given John has logged in with email "admin@admin.com" and password "admin"
    And he has clicked Change Profile Picture button

  Scenario:
    Given John clicks Upload new photo
    And he clicks the Crop this myself button without selecting a photo
    Then an error message should be shown telling John to select a photo

  Scenario:
    Given John clicks Upload new photo
    And he clicks the Crop this myself button with a photo selected
    When he sets the width and height field as 500 and accepts
    Then the size of the image should be 500 x 500
