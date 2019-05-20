#Feature: View Profile

  #Scenario: See a greeting on the profile page
    #Given John has logged in with email "john.gherkin.doe@travelea.com" and password "password"
    #Then he should see a greeting "Welcome, John!"

  #Scenario: See a default profile photo
    #Given John has logged in with email "john.gherkin.doe@travelea.com" and password "password"
    #And he has no profile photo
    #Then he should see a default profile photo

  #Scenario: View name in personal information
    #Given John has logged in with email "john.gherkin.doe@travelea.com" and password "password"
    #Then he should see a first name "John"
    #And a middle name: "Gherkin"
    #And a last name: "Doe"