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
            buildConfigField("String", "PRIVACY_POLICY", AppConfig.PRIVACY_POLICY)
            buildConfigField("String", "TERMS_OF_USE", AppConfig.TERMS_OF_USE)
            buildConfigField("String", "INSTANCE_ID", AppConfig.INSTANCE_ID)
            buildConfigField("String", "INSTANCE_KEY", AppConfig.INSTANCE_KEY)
            buildConfigField("int", "DASHBOARD_DISTANCE_LIMIT", AppConfig.DASHBOARD_DISTANCE_LIMIT)
            buildConfigField("String", "userServiceUrl", AppConfig.USER_SERVICE_URL)
            buildConfigField("String", "driveCoinUrl", AppConfig.DRIVE_COINS_URL)
            buildConfigField("String", "userStatisticsUrl", AppConfig.USER_STATISTICS)
            buildConfigField("String", "leaderboardUrl", AppConfig.LEADERBOARD_URL)
            buildConfigField("String", "tripEventTypeUrl", AppConfig.TRIP_EVENT_TYPE_URL)
            buildConfigField("String", "carServiceUrl", AppConfig.OBD_API_ENDPOINT)
            buildConfigField("String", "HERE_API_KEY", AppConfig.HERE_API_KEY)
            buildConfigField("String", "SOURCE", AppConfig.SOURCE)
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            buildConfigField("String", "PRIVACY_POLICY", AppConfig.PRIVACY_POLICY)
            buildConfigField("String", "TERMS_OF_USE", AppConfig.TERMS_OF_USE)
            buildConfigField("String", "INSTANCE_ID", AppConfig.INSTANCE_ID)
            buildConfigField("String", "INSTANCE_KEY", AppConfig.INSTANCE_KEY)
            buildConfigField("int", "DASHBOARD_DISTANCE_LIMIT", AppConfig.DASHBOARD_DISTANCE_LIMIT)
            buildConfigField("String", "userServiceUrl", AppConfig.USER_SERVICE_URL_DEV)
            buildConfigField("String", "driveCoinUrl", AppConfig.DRIVE_COINS_URL_DEV)
            buildConfigField("String", "userStatisticsUrl", AppConfig.USER_STATISTICS_DEV)
            buildConfigField("String", "leaderboardUrl", AppConfig.LEADERBOARD_URL_DEV)
            buildConfigField("String", "tripEventTypeUrl", AppConfig.TRIP_EVENT_TYPE_URL_DEV)
            buildConfigField("String", "carServiceUrl", AppConfig.OBD_API_ENDPOINT_DEV)
            buildConfigField("String", "HERE_API_KEY", AppConfig.HERE_API_KEY)
            buildConfigField("String", "SOURCE", AppConfig.SOURCE)
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
    implementation(AppDependencies.appLibraries)

    implementation(AppDependencies.retrofitLibraries)
    implementation(AppDependencies.encryptedSharedPref)

    implementation(project(Modules.domain))
    implementation(project(Modules.content))

    implementation(AppDependencies.trackingApi)
    implementation(AppDependencies.googleGuava)
}