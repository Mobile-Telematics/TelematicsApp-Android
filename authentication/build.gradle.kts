plugins {
    id("telematics.android.library")
}


android {
    namespace = "com.telematics.authentication"
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)

    implementation(libs.loginAuthFramework)
}