import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

object Plugins {

    private const val gradle = "com.android.tools.build:gradle:${PluginVersion.gradle}"
    private const val kotlin_gradle =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginVersion.kotlin}"
    private const val daggerHilt =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.daggerHilt}"

    private const val googleServices = "com.google.gms:google-services:${PluginVersion.googleServices}"
    private const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:${PluginVersion.firebaseCrashlytics}"

    val classpathList = arrayListOf(
        gradle,
        kotlin_gradle,
        daggerHilt,
        googleServices,
        firebaseCrashlytics
    )

    /*---*/

    const val application = "com.android.application"
    const val android = "android"
    const val androidLibrary = "com.android.library"
    const val kotlin = "kotlin"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinAndroid = "kotlin-android"
    const val daggerHiltPlugin = "dagger.hilt.android.plugin"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val googlePlugins = "com.google.gms.google-services"
    const val firebaseCrashlyticsPlugin = "com.google.firebase.crashlytics"
}

fun DependencyHandler.classpathList(list: List<String>) {
    list.forEach { dependency ->
        add(ScriptHandler.CLASSPATH_CONFIGURATION, dependency)
    }
}