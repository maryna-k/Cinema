# Cinema

Cinema is an Android application that allows to search movies by various categories, check movie details including its trailers and reviews and save the movie details and reviews to the device's internal database. The application works both on Android phones and tablets and allows the two-pane mode in the devices with wide screen. 
This is the second project for the Udacity Android Developer Nanodegree by Google. 

## Functionality

- Requests movies by genre or popularity from the TMDB API, fetches the JSON response and displays movie posters in a grid;
- Uses a drawer navigation to make search by different search criteria;
- Allows endless scroll on the grid;
- Displays details of a movie in a separate activity;
- Determines dominant color of the movie backdrop image and sets it as a background color of the several views and the toolbar;
- Displays movie trailers in the recycler view and allows to watch the trailer without switching to another application;
- Shows reviews preview in the DetailFragment and displays all the reviews in the recycler view of the ReviewFragment (phone implementation) or ReviewDialogFragment (tablet implementation);
- Shows empty views in case of absent trailer or review and progress bar in case of no internet connection. Automatically loads trailers and reviews as soon as connection is established;
- Shows empty view with appropriate message in place of the movie grid and automatically loads movies as soon as connection is established;
- Allows to add movie into favorites. Stores favorite movies in the internal database to allow offline access. Saves movie poster in the device's file system as a bitmap file and stores its path in the database. Saves movie reviews in a separate table of the database; 
- Synchronizes database with the server each 24 hours.

## Demo

#### Phone layout
<img src="https://drive.google.com/uc?export=view&id=0BzgPHmivHmCsWm44anYzUUFfTUk" height="500">
</br>
<img src="https://drive.google.com/uc?export=view&id=0BzgPHmivHmCsclVxd3QtX2JmT2c" height="500">
</br></br>

#### Tablet layout
<img src="https://drive.google.com/uc?export=view&id=0BzgPHmivHmCsTTV0cW5GRGlPS3M" height="400" >
</br></br>

## Libraries and APIs used

- TMDb API;
- YouTube Android Player API;
- Palette API;
- Picasso library;

## Pre-requisites

- minSdkVersion 16
- targetSdkVersion 25
- Android Build Tools v25.0.0

## Install

To run the application, download the zip file, unzip it, import project to the Android studio and run the app from the Run menu of the Android studio. This app uses the TMDb API (The Movie Database) and the YouTube Android Player API, both require the unique API keys. To get the keys, follow the steps at the following websites:

- https://www.themoviedb.org/
- https://developers.google.com/youtube/android/player/

Save your keys at Keys.java file in the utilities package.
