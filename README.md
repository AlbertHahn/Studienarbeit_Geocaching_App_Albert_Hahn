<img src="https://user-images.githubusercontent.com/55827001/105046346-4889fa00-5a69-11eb-84f7-87e3fc5d0e05.png" width="100%" height="100%">

# Geo Adventure is a geocaching application for Android. Developed for my "Android Development course" at the OTH-Amberg-Weiden.

Simple Description of the implemented functions:

## Login
The application starts with an animated splash screen, after the intro you are asked to log in to get into the actual app. There are two buttons, 
one to log in and the other to create an account. When clicking on the account we get the option to create a user. 
This will then be stored in an SQL database located locally on our device. Back from the account menu we can now log in with the newly created user data.



<p float="left">
<img src="https://user-images.githubusercontent.com/55827001/105046783-ce0daa00-5a69-11eb-8d58-b502d62ed3c2.gif" width="30%" height="30%" loop=infinite>
<img src="https://user-images.githubusercontent.com/55827001/105047591-ce5a7500-5a6a-11eb-9968-70ab3fbe5841.png" width="30%" height="30%">
</p>

## Main menu
The main menu is an activity with a Google Maps fragment, with zoom functionality and a toolbar filled with rich functions. 
We make the toolbar appear by clicking on the "burger" in the upper left corner or by dragging from the left side of the screen with the input device from left to right.

## Toolbar
### Navigation header 
This displays the user's name, as well as their level and accumulated experience.

<img src="https://user-images.githubusercontent.com/55827001/105044905-90a81d00-5a67-11eb-8ff0-c6b4d7385f4c.png" width="30%" height="30%">

### Geocaching
Is a subcategory of the toolbar, this includes functions such as hiding caches by scanning QR codes (https://github.com/zxing/zxing), 
a list of different objects that we can hide as caches and two lists that tell the user which caches are still to be found on the map 
and which ones have already been found by him. The geocaches can only be collected by other users.

<p float="left">
<img src="https://user-images.githubusercontent.com/55827001/105045722-85a1bc80-5a68-11eb-9d79-3f8f8f602169.png" width="30%" height="30%">
<img src="https://user-images.githubusercontent.com/55827001/105048372-c949f580-5a6b-11eb-8569-028fc0d41067.png" width="30%" height="30%">
</p>

### Settings
This contains functions such as activating geofences, activating the speech function when finding a cache, setting the geofence radius, 
saving the position data of the caches as a .gpx file and a button for exiting the main menu.
