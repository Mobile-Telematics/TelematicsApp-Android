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
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            debuggable(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders(mapOf(Pair("crashlyticsCollectionEnabled", true)))
            //signingConfig = signingConfigs.release
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            manifestPlaceholders(mapOf(Pair("crashlyticsCollectionEnabled", false)))
            //signingConfig = signingConfigs.debug
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
    //implementation(project(Modules.tracking))

    implementation(AppDependencies.lifecycleKtx)
    implementation(AppDependencies.daggerHiltLibraries)
    kapt(AppDependencies.daggerHiltCompiler)
    kapt(AppDependencies.daggerHiltAndroidXCompiler)

    // TODO: need to refactor
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("me.relex:circleindicator:2.1.4")
}