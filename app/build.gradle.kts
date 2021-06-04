plugins {
    id(Plugins.application)
    id(Plugins.daggerHiltPlugin)
    kotlin(Plugins.android)
    id(Plugins.kotlinKapt)
    id(Plugins.googlePlugins)
    id("kotlin-android")
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

            //signingConfig = signingConfigs.release
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            //signingConfig = signingConfigs.debug
        }
    }

    //val keystorePropertiesFile = rootProject.file("keystore/key.properties")

    signingConfigs {
        getByName("debug") {
            keyAlias = "raxelcore"
            keyPassword = "123456"
            storeFile = file("keystore/raxel.jks")
            storePassword = "123456"
        }
        create("release") {
            keyAlias = "raxelcore"
            keyPassword = "123456"
            storeFile = file("keystore/raxel.jks")
            storePassword = "123456"
        }
    }
//    flavorDimensions(AppConfig.dimension)
//    productFlavors {
//        create("prod") {
//            dimension(AppConfig.dimension)
//        }
//
//        create("dev") {
//            applicationIdSuffix = ".dev"
//            dimension(AppConfig.dimension)
//        }
//    }

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
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt(AppDependencies.daggerHiltCompiler)
    implementation(AppDependencies.retrofitLibraries)
    implementation(AppDependencies.lifecycleKtx)

    implementation(project(Modules.data))
    implementation(project(Modules.domain))
    implementation(project(Modules.dashboard))
    implementation(project(Modules.account))
    implementation(project(Modules.authentication))
    implementation(project(Modules.content))

    implementation("androidx.fragment:fragment-ktx:1.4.0-alpha02")
    implementation("androidx.fragment:fragment:1.4.0-alpha02")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")
}