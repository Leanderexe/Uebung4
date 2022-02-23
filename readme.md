# Abschluss

Files downloader and store the content in bson form in mongo database.

## Installation

Install java 8 and run runner.AppRunner class to the run application

## Description
The changes are done in FilesDownloader class, which downloads the files,
get the content and convert it into bson and save into the database,
also getting speaker data from the content and save them in separate collection of database.



1) One program to start the application: Please run runner.AppRunner class to run the application.

2) The changes are done in FilesDownloader class, which downloads the files,
   get the content and convert it into bson and save into the database,
   also getting speaker data from the content and save them in separate collection of database.

3) Convert xml files to bson and save into database this is implemented in FilesDownloader. Please see downloadAndSaveTheFilesContentInDbInBson
   method inside this class.

4) Duplication of the files are removed.

5) For local database connection, replace content of database.MongoDBConnectionHandler class with the content of Connection_local file inside project main directory


Note: Connection class is used to connect the program to database.
DatabaseOperation classs is the implementation of Operation interface.