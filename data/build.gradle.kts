plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.android)
    id(Plugins.kotlinKapt)
}


android {

    compileSdkVersion(AppConfig.compileSdk)

    defaultConfig {
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        minSdkVersion(AppConfig.minSdk)
        targetSdkVersion(AppConfig.targetSdk)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            debuggable(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "APP_ID", AppConfig.APP_ID)
            buildConfigField("String", "INSTANCE_ID", AppConfig.INSTANCE_ID_PROD)
            buildConfigField("String", "INSTANCE_KEY", AppConfig.INSTANCE_KEY_PROD)
            buildConfigField("String", "userServiceUrl", AppConfig.USER_SERVICE_URL_DEV)
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            buildConfigField("String", "APP_ID", AppConfig.APP_ID)
            buildConfigField("String", "INSTANCE_ID", AppConfig.INSTANCE_ID_PROD)
            buildConfigField("String", "INSTANCE_KEY", AppConfig.INSTANCE_KEY_PROD)
            buildConfigField("String", "userServiceUrl", AppConfig.USER_SERVICE_URL_DEV)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(AppDependencies.moduleLibraries)

    implementation(AppDependencies.retrofitLibraries)
    implementation(AppDependencies.encryptedSharedPref)

    implementation(project(Modules.domain))

}