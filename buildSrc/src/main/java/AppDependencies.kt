import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {

    const val trackingApi = "com.telematicssdk:tracking:${Versions.trackingApi}"
    const val loginAuthFramework = "com.telematicssdk:auth:${Versions.loginAuthFramework}"

    //std lib
    private const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    //android ui
    private const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    private const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    private const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    private const val material = "com.google.android.material:material:${Versions.material}"
    private const val androidxActivity = "androidx.activity:activity:${Versions.androidxActivity}"
    private const val androidxFragment = "androidx.fragment:fragment:${Versions.androidxFragment}"
    const val cropView = "com.isseiaoki:simplecropview:${Versions.cropView}"
    const val chartView = "com.github.PhilJay:MPAndroidChart:${Versions.chartView}"
    const val circleIndicatorView = "me.relex:circleindicator:${Versions.circleIndicatorView}"
    const val swipeToRefresh =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeToRefresh}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val countryCodePicker = "com.hbb20:ccp:${Versions.countryCodePicker}"

    //di hilt
    const val daggerHiltCompiler =
        "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"
    private const val daggerHilt = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val daggerHiltAndroidXCompiler =
        "androidx.hilt:hilt-compiler:${Versions.daggerHiltAndroidX}"

    //javax
    private const val javaxInject = "javax.inject:javax.inject:${Versions.javaxInject}"
    private const val javaxAnnotations = "javax.annotation:jsr250-api:${Versions.javaxAnnotations}"

    //security-crypto
    const val encryptedSharedPref =
        "androidx.security:security-crypto:${Versions.encryptedSharedPref}"

    //coroutines
    private const val coroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    private const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    //retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverterGson =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitRxJava2Adapter = "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0"
    const val retrofitRxJava3Adapter =
        "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofitRxAdapter}"
    const val retrofitUrlManager = "me.jessyan:retrofit-url-manager:1.4.0"

    //okhttp
    const val okHttp3Logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLogging}"

    // Gson
    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    //lifecycle
    val lifecycleKtx = arrayListOf(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}",
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    )

    //navigation
    private const val navigationFragmentKtx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigationKtx}"
    private const val navigationUiKtx =
        "androidx.navigation:navigation-ui-ktx:${Versions.navigationKtx}"

    //firebase
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    private const val firebaseDatabaseKtx =
        "com.google.firebase:firebase-database-ktx"
    private const val firebaseAuth = "com.google.firebase:firebase-auth"
    private const val firebaseStorage =
        "com.google.firebase:firebase-storage-ktx"
    const val firebaseCrashlytics =
        "com.google.firebase:firebase-crashlytics-ktx"

    //google
    private const val googlePlayServices =
        "com.google.android.gms:play-services-auth:${Versions.googlePlayServices}"
    const val googleGuava = "com.google.guava:guava:${Versions.googleGuava}"


    val appLibraries = arrayListOf(
        kotlinStdLib,
        coreKtx,
        appcompat,
        constraintLayout,
        material,
        encryptedSharedPref,
        coroutines,
        coroutinesAndroid,
        androidxActivity,
        androidxFragment
    )

    val daggerHiltLibraries = arrayListOf(
        daggerHilt,
        daggerHiltAndroidXCompiler
    )

    val moduleLibraries = arrayListOf(
        kotlinStdLib,
        coroutines,
        javaxInject,
        javaxAnnotations
    )

    val retrofitLibraries = arrayListOf(
        retrofit,
        retrofitConverterGson,
        retrofitRxJava2Adapter,
        retrofitRxJava3Adapter,
        retrofitUrlManager,
        okHttp3Logging,
        gson
    )

    val navigateLibraries = arrayListOf(
        navigationFragmentKtx,
        navigationUiKtx
    )

    val firebase = arrayListOf(
        firebaseDatabaseKtx,
        firebaseAuth,
        firebaseStorage,
        googlePlayServices
    )
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}