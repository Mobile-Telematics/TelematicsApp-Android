plugins {
    id("telematics.android.feature")
}

android {
    namespace = "com.telematics.features.account"
}

dependencies {
    implementation(libs.cropView)
}