# TelematicsApp-Android with Firebase integration
![](https://github.com/Mobile-Telematics/TelematicsAppFirebase-iOS/raw/master/img_readme/mainlogo.jpg)

## Description
This Telematics App is created by DATA MOTION PTE. LTD. and is distributed free of charge to all customers & users and can be used to create your own application for iOS in a few steps.

## Initial app setup & credentials
For commercial use, you need to create a sandbox account https://userdatahub.com/user/registration and get `InstanceID` and`InstanceKEY` auth keys to work with our API

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
