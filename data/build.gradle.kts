plugins {
    id("telematics.android.library")
    id("telematics.android.room")
    id("telematics.android.retrofit")
    id("telematics.android.hilt")
}


android {
    namespace = "com.telematics.data"

    defaultConfig {
        buildConfigField("int", "DB_VERSION", "1")

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        getByName("release") {
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
            buildConfigField("String", "SOURCE", AppConfig.SOURCE)
            buildConfigField("boolean", "REQUEST_NOTIFICATION_PERMISSION", AppConfig.REQUEST_NOTIFICATION_PERMISSION)
        }
        getByName("debug") {
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
            buildConfigField("String", "SOURCE", AppConfig.SOURCE)
            buildConfigField("boolean", "REQUEST_NOTIFICATION_PERMISSION", AppConfig.REQUEST_NOTIFICATION_PERMISSION)
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":content"))

    implementation(libs.javax.inject)
    implementation(libs.javax.annotation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment)

    implementation(libs.trackingApi)
    implementation(libs.loginAuthFramework)
}