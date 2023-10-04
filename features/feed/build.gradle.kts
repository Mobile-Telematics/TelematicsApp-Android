plugins {
    id("telematics.android.feature")
}

android {
    namespace = "com.telematics.feed"

    defaultConfig {
        manifestPlaceholders["MAPS_API_KEY"] = AppConfig.GOOGLE_MAP_API
    }
}

dependencies {
    implementation(libs.google.play.services.maps)
}
