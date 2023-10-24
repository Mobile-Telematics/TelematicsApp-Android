plugins {
    id("telematics.android.feature")
}

android {
    namespace = "com.telematics.dashboard"
}

dependencies {
    implementation(project(":core:sdkhelper"))
    implementation(libs.chartView)
    implementation(libs.circleIndicatorView)
}