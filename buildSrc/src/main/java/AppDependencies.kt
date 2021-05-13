import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {

    //std lib
    private const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    //android ui
    private const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    private const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    private const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    private const val material = "com.google.android.material:material:${Versions.material}"

    //di hilt
    const val daggerHiltCompiler =
        "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"
    private const val daggerHilt = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    private const val daggerHiltViewModel =
        "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.daggerHiltAndroidX}"
    private const val daggerHiltAndroidXCompiler =
        "androidx.hilt:hilt-compiler:${Versions.daggerHiltAndroidX}"

    //javax
    private const val javaxInject = "javax.inject:javax.inject:${Versions.javaxInject}"
    private const val javaxAnnotations = "javax.annotation:jsr250-api:${Versions.javaxAnnotations}"

    //security-crypto
    private const val encryptedSharedPref =
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
    const val okHttp3 = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttp3Logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLogging}"

    // Gson
    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    //lifecycle
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"


    val appLibraries = arrayListOf(
        kotlinStdLib,
        coreKtx,
        appcompat,
        constraintLayout,
        material,
        encryptedSharedPref,
        coroutines,
        coroutinesAndroid
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
        okHttp3,
        okHttp3Logging,
        gson
    )
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}