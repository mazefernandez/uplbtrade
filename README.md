# UPLB Trade: A Mobile E-Marketplace for UPLB STUDENTS
Authors: Anna-Mae Caitlin A. Fernandez and Juan Miguel III J. Bawagan   

This study focused on the development of UPLB Trade. It is a mobile application that provides an avenue for LosBaños students to buy and sell second-hand materials with theirfellow students. The application also arranges meet-ups, allows messaging, suggests items, and lets users report any suspicioususers and items. This study also focused on the development of an Admin Web Application that is used to maintain UPLB Trade and provide support to its users. The technologies used for this study are Android Studio, Firebase, MySQL, Node.js, AngularJS, and Google Maps API. To assess the application, the System Usability Scale (SUS) was used. Based on the results of the usability test, the application gained an overall rating of 74.7. According to the SUS adjectives, a SUS score of 71.4 is classified as ’Good’. Since UPLB Trade’s SUS score of 74.7 surpasses this threshold, it supports that the respondents were satisfied with the application’s usability.

Keywords: e-commerce, mobile commerce, e-marketplace, UPLB, C2C, System Usability Scale

# Development Set-up
## Prerequisites
* Node.JS
* npm
* MySQL 
* Android phone
* Android Studio 
## Instructions for Admin Panel and Database 
uplbtrade_api/
1. Install npm from [npm](https://www.npmjs.com/package/download)
2. Run the following command to install other packages:
```
npm install
```
3. Run the following command to setup database:
```
npm run setup
```
4. Run the following command to run the server: 
```
npm run start
```
## Instructions for Android Application 
uplbtrade/
1. Install Android Studio from [android-studio](https://developer.android.com/studio)
2. Choose 'Install Android Emulator' in Android Studio or connect Android device to PC
3. Turn on 'Developer Mode' on Android device. See [developer-mode](https://developer.android.com/studio/debug/dev-options)
4. To setup project in Android Studio click Build > Make Project
5. To choose device click Device Manager > Create Device or click Device Manager > Physical > Pair Device 
6. To run the application click Run > Run app 
