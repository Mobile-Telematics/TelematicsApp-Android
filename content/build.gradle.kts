plugins {
    id("telematics.android.library")
}


android {
    namespace = "com.telematics.content"
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)

    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.google.material)
}