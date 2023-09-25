plugins {
    id(Plugins.application)
    id(Plugins.daggerHiltPlugin)
    kotlin(Plugins.android)
    id(Plugins.kotlinKapt)
    id(Plugins.googlePlugins)
    id(Plugins.firebaseCrashlyticsPlugin)
    id(Plugins.kotlinAndroid)
}

android {

    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = AppConfig.applicationId
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
        }
    }

    /*signingConfigs {
        getByName("debug") {
            keyAlias = "key0"
            keyPassword = "******"
            storeFile = file("path_to_file/keystore.jks")
            storePassword = "******"
        }
        create("release") {
            keyAlias = "key0"
            keyPassword = "******"
            storeFile = file("path_to_file/keystore.jks")
            storePassword = "******"
        }
    }*/

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

    //dynamicFeatures = DynamicModules.modules
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(AppDependencies.appLibraries)

    implementation(AppDependencies.daggerHiltLibraries)
    kapt(AppDependencies.daggerHiltCompiler)
    implementation(AppDependencies.retrofitLibraries)
    implementation(AppDependencies.lifecycleKtx)
    implementation(AppDependencies.navigateLibraries)
    implementation(platform(AppDependencies.firebaseBom))
    implementation(AppDependencies.firebaseCrashlytics)

    implementation(project(Modules.data))
    implementation(project(Modules.domain))
    implementation(project(Modules.content))
    implementation(project(Modules.authentication))
    implementation(project(Modules.feed))
    implementation(project(Modules.dashboard))
    implementation(project(Modules.account))
    implementation(project(Modules.leaderboard))
    implementation(project(Modules.reward))
    implementation(project(Modules.obd))

    implementation(AppDependencies.countryCodePicker)

    implementation(AppDependencies.trackingApi)
    implementation(AppDependencies.circleIndicatorView)
    implementation(AppDependencies.roomRuntime)
    kapt(AppDependencies.roomCompiler)
}