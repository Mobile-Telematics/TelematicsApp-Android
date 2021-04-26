plugins {
    id(Plugins.application)
    kotlin(Plugins.android)
    kotlin(Plugins.androidExtensions)
}


android {

    compileSdkVersion(AppConfig.compileSdk)

    defaultConfig {
        applicationId = AppConfig.applicationId
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

    flavorDimensions(AppConfig.dimension)
    productFlavors {
        create("prod") {
            dimension(AppConfig.dimension)
        }

        create("dev") {
            applicationIdSuffix = ".dev"
            dimension(AppConfig.dimension)
        }
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    //dynamicFeatures = DynamicModules.modules
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(AppDependencies.appLibraries)

    implementation(project(Modules.data))
    implementation(project(Modules.account))
    implementation(project(Modules.dashboard))
}