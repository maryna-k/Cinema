# Cinema

This is the second project for  Android Developer Nanodegree by Udacity. 

## Pre-requisites

- Android SDK v24
- Android Build Tools v24.0.3
- Android Support Repository v24.2.1

## Install

This sample uses the Gradle build system. To build this project, use "Import Project" in Android Studio.
The TMDB API requires the API key. To get it, check the details on the TMDB website. The API key should be inserted at the buil.gradle file.

## Functionality

- Requests movies by genre or popularity from the TMDB API, fetches the JSON response and displays movie posters in a grid;
- Allows endless scroll on the grid;
- Displays details of a particular movie in a separate activity;
- Allows to add movie into favorites. Stores favorite movies in the internal database to allow offline access.