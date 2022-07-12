# gistValidation Project

[![Build Status](https://app.travis-ci.com/alexandrera/gistValidation.svg?branch=main)](https://app.travis-ci.com/alexandrera/gistValidation)

## Description:
The idea with this project is to validate a REST API by using most used HTTP methods like GET, POST, PATCH, PUT, DELETE with rest assured framework and TestNG

Note 1: Project was developed and tested in windows 11, but can be executed in linux as well with some modifications (mostly environment variables configuration)

Note 2: JDK 17 was used with no special reason

Note 3: IntelliJ Idea Community version was used to develop

Note 4: The way the project is, it is possible to just clone it and execute it after OS Settings in Instructions below:

Issues faced:
 - Initially it was used JUnit and there is a limitation with variable within the test methods. It is not possible to use variables from different methods.
 - TestNG was used instead since it has an annotation to workaround it (https://testng.org/doc/documentation-main.html#dependencies-with-annotations)
 - Github automatically removes the bearer token generated in case it is used in the code (which makes sense) so the bearer was hidden from the code using Base64 to decoded this data. It makes more sense to use it as a system variable in order to make authentication safer since we can simple make a print of the decoded string.
 
## Instructions:

### OS Settings:
 1. Install Java JDK 17 (https://download.oracle.com/java/17/archive/jdk-17.0.3.1_windows-x64_bin.msi).
 
 2. Install maven version 3.8.6 (https://maven.apache.org/install.html).
 
 3. Make sure JDK and Maven are both in the environments variable path.
 
### Github Bearer token Settings
 1. Access your github account and generate a bearer token (https://github.com/settings/tokens).
 
 ![image](https://user-images.githubusercontent.com/15263937/178337611-a4db0a45-ccc3-498d-96aa-4d24424bbac9.png).

 2. After it is generated uncomment the main method lines, put you password there, and execute the code. 
 
 ![image](https://user-images.githubusercontent.com/15263937/178337213-933b93ce-6f2d-428c-9c68-bac8b55862bb.png).
 
 3. It will print the encoded bearer token in the console, after that remove you token and comment the lines again. Or use just use: https://www.base64encode.org/
 4. Make sure to use the text: "Bearer " with single space plus the token like this: Bearer aBcD1234
 5. Put the encoded toke in the variable getToken in myTest class.
 
 ### Execution:
  1. You can either run from IDE Intellij Idea or Eclipse by executing the myTest class or
  
  ![image](https://user-images.githubusercontent.com/15263937/178339707-87bf3542-928c-427e-a94c-3a71d11f0f8a.png)

  2. By running "mvn test" from project folder in your machine
  
  ![image](https://user-images.githubusercontent.com/15263937/178339604-2bcdbe35-f0b2-4fdc-bd11-1a37b5437c64.png)
  
  3. After the execution a report is generated with the name Spark.html under target folder in the IDE. It looks like this:
  
  ![image](https://user-images.githubusercontent.com/15263937/178340061-51b11679-b324-4c00-a77e-408426ad2808.png)
  
  4. Travis CI integration was implemented but build is failing due to an error related to maven surefire
  
### To be improved:
 - Use the encoded token as a system variable, this way the encryption will not be necessary
 - Fix Travis CI Build (new branch opened to fix this issue)

### Frameworks used:
 - Rest Assured: https://rest-assured.io/
 - TestNG: https://testng.org/doc/index.html
 - Extent Reports: https://www.extentreports.com/
