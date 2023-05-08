# Fundamentals
Fundamentals is a group of plugins developed for Beta 1.7.3 aiming to provide essential functionality. 

Please note, these plugins are primarily designed for RetroMC and may not work on other servers.

# How To Build
1. Clone this repository
2. Open the project in your IDE of choice
3. Run `mvn clean package` in the root directory of the project
-  Maven will automatically download the required dependencies 
-  Dependencies that are not available on Maven Central are downloaded to ./lib
-  You may need to add this folder as a library in your IDE as it is not automatically added
4. The compiled jars will be located in /target of the respective plugin