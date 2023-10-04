plugins {
    id("telematics.android.feature")
}

android {
    namespace = "com.telematics.dashboard"
}

dependencies {
    implementation(libs.chartView)
    implementation(libs.circleIndicatorView)
}