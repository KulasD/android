# Lokalizator WiFi
Old network utility app for school project.

## Technologies 
- Android Studio
- Java
- SqLite
- MPAndroidChart
- ImageMap

## Features
- The application provides access to a dynamically updated chart, which shows the signal strengths of all networks within the range of the device.
- Informations about nearby networks.
- Aditional informations about the connected network.
- The application allows to determine the approximate location of the user based on the surrounding WiFi networks.

## Setup
To run the application you must:
1. Clone the application repository.
2. Run the emulator.
3. Run the command line and go to the project's location.
4. Run the 'npm install' command.
5. To install and run the application, simply use 'react-native run-android' command.

## Showcase
- home screen after pressing the get wifi info button
<img src="https://user-images.githubusercontent.com/43846211/122756973-5f8e7b80-d297-11eb-9591-5924fbe75bf2.jpg" height="600">

- graph screen 
<img src="https://user-images.githubusercontent.com/43846211/122757740-49cd8600-d298-11eb-83cb-b870894b227a.jpg" width="600">

- network information screen - choose wifi name from home screen
<div>
  <p>Informations of not connected wifi.</p>
  <img src="https://user-images.githubusercontent.com/43846211/122758228-d710da80-d298-11eb-841b-35cec57a74a0.jpg" height="600">
  <p>Informations of connected wifi.</p>
  <img src="https://user-images.githubusercontent.com/43846211/122758231-d8420780-d298-11eb-99cb-0b925db88b86.jpg" height="600">
</div>

- Localization Menu Screen - 
allows user to go to the localization configuration (Configure button) or to the localization itself (Localize button).
<img src="https://user-images.githubusercontent.com/43846211/122761564-acc11c00-d29c-11eb-8c92-0a486ebb6cfd.jpg" height="600">

- Configure Screen - includes options to calibrate wifi localization. Before starting the calibration, the user must go to the Choose Wifis screen 
to select the wifi networks that are always available wherever they are. After doing this, select the pin on the map and press the Calibrate button. 
After 30 seconds, the selected pin will turn yellow, it means that the required data has been saved.
<img src="https://user-images.githubusercontent.com/43846211/122762929-18f04f80-d29e-11eb-8d7f-9c7fe5bbdc11.jpg" width="600">

- Choose wifis screen
<img src="https://user-images.githubusercontent.com/43846211/122763224-6076db80-d29e-11eb-8c87-c0fda48c8a43.jpg" width="600">

- Localize screen - it is used to locate the user on the basis of previously configured data (Configure screen). 
The user, after pressing the Locate button and waiting 30 seconds, will get information on the screen with the name of the place where he currently is.
<img src="https://user-images.githubusercontent.com/43846211/122763329-800e0400-d29e-11eb-883f-99ec7c878a06.jpg" width="600">
