plugins {
    id(Plugins.application)
    id(Plugins.daggerHiltPlugin)
    kotlin(Plugins.android)
    id(Plugins.kotlinKapt)
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
            buildConfigField("String", "userUrl", "\"https://user.telematicssdk.com/\"")
            //signingConfig = signingConfigs.release
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            buildConfigField("String", "userUrl", "\"https://user.telematicssdk.com/\"")
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

    implementation(project(Modules.data))
    implementation(project(Modules.dashboard))
    implementation(project(Modules.account))
}