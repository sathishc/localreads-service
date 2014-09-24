LocalReads Web Service
======================

LocalReads Web Service is a Grails/Groovy based backend service used by the LocalReads mobile app. The service has rest 
apis that allows user to login, add Books, create wishlists or ownerships, and search for Books nearby.  

## Using this project

This project implements the backend for the LocalReads app. To get the front-end app code see https://github.com/sathishc/localreads
To use this service all you need is to install MongoDB in your local machine and create a database called localreads_dev (for development)
See the config.groovy and Data-source.groovy files for more information

## Building this project

Building a grails project is extremely easy. All you need to have is JDK version 6 or above in the machine and set JAVA_HOME
to the correct location. Clone this repository from Github and run "./grailsw compile". This will download all necessary
libraries and plugins and compile the project. For more information check http://grails.org/doc/latest/   
