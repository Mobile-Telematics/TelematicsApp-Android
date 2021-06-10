# TelematicsApp-Android with Firebase© integration
![](https://github.com/Mobile-Telematics/TelematicsAppFirebase-iOS/raw/master/img_readme/mainlogo.jpg)

## Description
This Telematics App is created by DATA MOTION PTE. LTD. and is distributed free of charge to all customers & users and can be used to create your own application for iOS in a few steps.

## Basic Concepts & Credentials
For commercial use, you need to create a sandbox account https://userdatahub.com/user/registration and get `InstanceID` and`InstanceKEY` auth keys to work with our API

Additionally, to authenticate users in your app and store users data, you need to create a Firebase© account: https://firebase.google.com

All user data will be stored in the Firebase© Realtime Database, which will allow you to create an app users database without programming skills in a few minutes.

## Setup Firebase© Project
Step 1: After creating your Firebase© account, open your console: https://console.firebase.google.com

Click "Create a project" button.

![](https://github.com/Mobile-Telematics/TelematicsAppFirebase-iOS/raw/master/img_readme/f01.png)

Step 2: Enter the name of your future Project. Click "Continue" button.

![](https://github.com/Mobile-Telematics/TelematicsAppFirebase-iOS/raw/master/img_readme/f02.png)

Step 3: For ease of integration, at the next step, we recommend deactivating the "Enable Google Analytics" checkbox.

![](https://github.com/Mobile-Telematics/TelematicsAppFirebase-iOS/raw/master/img_readme/f03.png)

Click "Create project".

Step 4: Now you need to create a configuration for your Android app. Click on the "Android" as it us shown on the picture below:

## Setup TelematicsApp Configuration
In file AppConfig.kt you can specify the basic settings for your app.
To use your unique applicationId for your application, change applicationIdPrefix and name:
const val applicationIdPrefix = "com.your_application_prefix"
private const val name = "your_application_name"

To work with our API use InstanceId and InstanceKey from https://userdatahub.com/user/registration :
const val INSTANCE_ID_PROD = "\"YOUR_INSTANCE_ID\"" 
const val INSTANCE_KEY_PROD = "\"YOUR_INSTANCE_KEY\"" 

To set application label change app_name in strings.xml in content module:
<string name="app_name">YOUR_LABEL</string>

To set application icon, find the content module icon in resource folders (res/mipmap, res/mipmap-hdpi, etc.) and replace it. And for change background icon color set ic_launcher_background in color.xml:
<color name="ic_launcher_background">#your_color</color>

To connect your Firebase you need to add the google-services.json file to project_directory\app. Final file path: project_directory\app\google-services.json
