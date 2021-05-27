// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val kotlin_version by extra("1.4.20")
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.android.tools.build:gradle:3.3.2")
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