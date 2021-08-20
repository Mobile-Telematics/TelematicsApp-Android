buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpathList(Plugins.classpathList)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://s3.us-east-2.amazonaws.com/android.telematics.sdk.production/") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}