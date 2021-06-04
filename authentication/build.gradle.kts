plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.android)
    id(Plugins.kotlinKapt)
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
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(AppDependencies.moduleLibraries)
    implementation(project(Modules.domain))
    implementation(project(Modules.data))

    implementation("com.google.firebase:firebase-bom:28.0.1")
    implementation("com.google.firebase:firebase-database-ktx:20.0.0")
    implementation("com.google.android.gms:play-services-auth:19.0.0")

    //implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation("com.google.firebase:firebase-auth:21.0.1")
}