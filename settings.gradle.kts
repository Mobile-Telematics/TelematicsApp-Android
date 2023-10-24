pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //jcenter() // Warning: this repository is going to shut down soon
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://s3.us-east-2.amazonaws.com/android.telematics.sdk.production/") }
        maven { url = uri("file://${rootDir}/repo") }
    }
}
include(
    ":app",
    ":data",
    ":domain",
    ":content",
    ":authentication",
    ":features:account",
    ":features:dashboard",
    ":features:feed",
    ":features:leaderboard",
    ":features:reward",
    ":features:obd",
    ":core:sdkhelper"
)
rootProject.name = "Telematics"
