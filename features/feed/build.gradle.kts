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
        //applicationId = "${AppConfig.applicationIdPrefix}.dashboard"
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        minSdkVersion(AppConfig.minSdk)
        targetSdkVersion(AppConfig.targetSdk)

        manifestPlaceholders["MAPS_API_KEY"] = AppConfig.GOOGLE_MAP_API
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            debuggable(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
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
    implementation(AppDependencies.recyclerView)
    implementation(AppDependencies.googleMap)
}
