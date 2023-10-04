plugins {
    id("telematics.android.feature")
}

android {
    namespace = "com.telematics.obd"
}

dependencies {
    implementation(libs.cropView)
    implementation(libs.circleIndicatorView)
}
