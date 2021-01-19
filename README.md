# Geo Adventure is a geocaching application for Android, developed for my "Android Development course" at the OTH-Amberg-Weiden.

Simple Description of the implemented functions:

## Login
The application starts with an animated splash screen, after the intro you are asked to log in to get into the actual app. There are two buttons, 
one to log in and the other to create an account. When clicking on the account we get the option to create a user. 
This will then be stored in an SQL database located locally on our device. Back from the account menu we can now log in with the newly created user data.

## Main menu
The main menu is an activity with a Google Maps fragment, with zoom functionality and a toolbar filled with rich functions. 
We make the toolbar appear by clicking on the "burger" in the upper left corner or by dragging from the left side of the screen with the input device from left to right.

## Toolbar
### Navigation header 
This displays the user's name, as well as their level and accumulated experience.
<img src="https://user-images.githubusercontent.com/55827001/105044905-90a81d00-5a67-11eb-8ff0-c6b4d7385f4c.png" width="30%" height="30%">



## Geocaching
Is a subcategory of the toolbar, this includes functions such as hiding caches by scanning QR codes, 
a list of different objects that we can hide as caches and two lists that tell the user which caches are still to be found on the map 
and which ones have already been found by him. The geocaches can only be collected by other users.

## Settings
This contains functions such as activating geofences, activating the language function when finding a cache, setting the geofence radius, 
saving the position data of the caches as a .gpx file and a button for exiting the main menu.
