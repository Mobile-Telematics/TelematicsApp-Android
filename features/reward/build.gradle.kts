plugins {
    id(Plugins.androidLibrary)
    id(Plugins.daggerHiltPlugin)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinKapt)
    id(Plugins.kotlinAndroidExtensions)
}

android {

    compileSdkVersion(AppConfig.compileSdk)

    defaultConfig {
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        minSdkVersion(AppConfig.minSdk)
        targetSdkVersion(AppConfig.targetSdk)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            debuggable(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders(
                mapOf(
                    "crashlyticsCollectionEnabled" to true,
                    "hereMapsLicenceKey" to AppConfig.HERE_LICENCE_KEY,
                    "hereMapsAppID" to AppConfig.HERE_APP_ID,
                    "hereMapsAppToken" to AppConfig.HERE_APP_CODE
                )
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            manifestPlaceholders(
                mapOf(
                    "crashlyticsCollectionEnabled" to false,
                    "hereMapsLicenceKey" to AppConfig.HERE_LICENCE_KEY,
                    "hereMapsAppID" to AppConfig.HERE_APP_ID,
                    "hereMapsAppToken" to AppConfig.HERE_APP_CODE
                )
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(AppDependencies.appLibraries)

    implementation(project(Modules.domain))
    implementation(project(Modules.data))
    implementation(project(Modules.content))

    implementation(AppDependencies.navigateLibraries)

    implementation(AppDependencies.daggerHiltLibraries)
    kapt(AppDependencies.daggerHiltCompiler)
    kapt(AppDependencies.daggerHiltAndroidXCompiler)

    implementation(AppDependencies.swipeToRefresh)
    implementation(AppDependencies.chartView)
}
