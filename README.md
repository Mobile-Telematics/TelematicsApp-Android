# TelematicsApp-Android with Firebase© integration
![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/telematicsapp.jpeg)

## Description
This Telematics App is created by DATA MOTION PTE. LTD. and is distributed free of charge to all customers & users and can be used to create your own application for Android in a few steps.

## Basic Concepts & Credentials
For commercial use, you need to create a sandbox account https://userdatahub.com/user/registration and get `InstanceID` and`InstanceKEY` auth keys to work with our API

Additionally, to authenticate users in your app and store user data, you need to create a Firebase© account: https://firebase.google.com

All user data will be stored in the Firebase© Realtime Database, which will allow you to create an app users database without programming skills in a few minutes.

## Setup Firebase© Project
Step 1: After creating your Firebase© account, open your console: https://console.firebase.google.com

Click "Create a project" button.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/1.png)

Step 2: Enter the name of your future Project. Click "Continue" button.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/2.png)

Step 3: For ease of integration, at the next step, we recommend deactivating the "Enable Google Analytics" checkbox.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/3.png)

Click "Create project".

Step 4: Now you need to create a configuration for your Android app. Click on the "Android" as it us shown on the picture below:
![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/4.png)

Step 5: Enter your Android Package Name. Enter the SHA-1 key, this identifier must be used in your application in Android Studio. Click "Register app" then.
![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/5.png)

Step 6: To connect your Firebase you need to add the `google-services.json` file to `project_directory\app`. Final file path: `project_directory\app\google-services.json`
![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/6.png)

Step 7: You can skip the "Add Firebase SDK" & "Add initialization code" steps below, because we already did it for you in our Telematics App:) Finish the setup and click on "Continue to console".
![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/7.png)

Step 8: Important. In order for your users to create accounts to log into your app, you need to go to "Authentication" section on the left side of the menu.
![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/8.png)

Step 9: In the "Sign-in method" tab, click on the Provider's "Email/Password" on the right "pencil" (Edit configuration hint) as in the picture below. If you need to perform authorization using the "Phone" Provider - select the setting of this item.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/9.png)

Step 10: Switch to "Enable" and click "Save" button. Now your users can login to the app.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/10.png)

Step 11: We need to activate Firebase© Realtime Database. This will allow you to store the data of all your users in this simple web interface. Go to the Realtime Database section on the left side of the menu and click on the "Create Database" button.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/11.png)

Step 12: Choose any Realtime Database location value.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/12.png)

Step 13: Select "Start a locked mode" and click the "Enable" button.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/13.png)

Step 14: Now you need to change rules for your Realtime Database. You need to go to “Realtime Database” section on the left side of the menu. In the “Rules” tab change read and write fields to “auth.uid != null”.

![](https://github.com/Mobile-Telematics/TelematicsApp-Android/blob/master/img_readme/14.png)

Step 15: Open our TelematicsApp in Android Studio, make sure to transfer the `google-services.json` file to project_directory\app (See Step 5 above) and Enjoy! 

Build & Run!


## Setup TelematicsApp Configuration
In file AppConfig.kt you can specify the basic settings for your app.
To use your unique applicationId for your application, change applicationIdPrefix and name:<br/>
const val applicationIdPrefix = "com.your_application_prefix"<br/>
private const val name = "your_application_name"

To work with our API use InstanceId and InstanceKey from https://userdatahub.com/user/registration :<br/>
const val INSTANCE_ID_PROD = "YOUR_INSTANCE_ID"<br/>
const val INSTANCE_KEY_PROD = "YOUR_INSTANCE_KEY" 

To set application label change app_name in strings.xml in content module:<br/>
<string name="app_name">YOUR_LABEL</string>

To set Privacy Policy change PRIVACY_POLICY in AppConfig.kt file :<br/>
const val PRIVACY_POLICY = "YOUR_PRIVACY_POLICY_LINK" //for example "https://www.telematicssdk.com/privacy-policy/"

To set Terms Of Use change PRIVACY_POLICY in AppConfig.kt file : <br/>
const val TERMS_OF_USE = "YOUR_TERMS_OF_USE_LINK" //for example"https://www.telematicssdk.com/privacy-policy/"


To set application icon, find the content module icon in resource folders (res/mipmap, res/mipmap-hdpi, etc.) and replace it. And for change background icon color set ic_launcher_background in color.xml:
<color name="ic_launcher_background">#your_color</color>

## Dashboard features

Our goal is to provide your users with a user-friendly interface to get the best user experience.
We suggest you use 2 (two) dashboards with Scoring and user Statistics data in your application. To get the first data, the user usually needs to drive a short distance. We set this parameter in the configuration file `AppConfig.kt` in parameter
const val DASHBOARD_DISTANCE_LIMIT = "10" //measured in km
