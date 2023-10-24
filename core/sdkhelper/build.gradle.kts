plugins {
    id("telematics.android.library")
}

android {
    namespace = "com.telematics.core.sdkhelper"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
}