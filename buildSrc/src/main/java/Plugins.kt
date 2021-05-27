import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

object Plugins {

    private const val gradle = "com.android.tools.build:gradle:${PluginVersion.gradle}"
    private const val kotlin_gradle =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginVersion.kotlin}"
    private const val daggerHilt =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.daggerHilt}"

    private const val googleServices = "com.google.gms:google-services:4.3.8"

    val classpathList = arrayListOf(
        gradle,
        kotlin_gradle,
        daggerHilt,
        googleServices
    )

    /*---*/

    const val application = "com.android.application"
    const val android = "android"
    const val androidExtensions = "android.extensions"
    const val androidLibrary = "com.android.library"
    const val javaLibrary = "java-library"
    const val kotlin = "kotlin"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinAndroid = "kotlin-android"
    const val daggerHiltPlugin = "dagger.hilt.android.plugin"
    const val dynamicFeature = "com.android.dynamic-feature"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val googlePlugins = "com.google.gms.google-services"
}

fun DependencyHandler.classpathList(list: List<String>) {
    list.forEach { dependency ->
        add(ScriptHandler.CLASSPATH_CONFIGURATION, dependency)
    }
}