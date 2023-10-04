plugins {
    id("telematics.android.library")
    id("telematics.android.room")
}


android {
    namespace = "com.telematics.domain"
}

dependencies {
    implementation(libs.androidx.recyclerview)

    implementation(libs.kotlinx.coroutines.core)

}