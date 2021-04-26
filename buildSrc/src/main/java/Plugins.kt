import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

object Plugins {

    private const val gradle = "com.android.tools.build:gradle:${PluginVersion.gradle}"
    private const val kotlin_gradle =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginVersion.kotlin}"
    private const val daggerHilt =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.daggerHilt}"

    val classpathList = arrayListOf(
        gradle,
        kotlin_gradle,
        daggerHilt
    )

    /*---*/

    const val application = "com.android.application"
    const val android = "android"
    const val androidExtensions = "android.extensions"
    const val androidLibrary = "com.android.library"
    const val javaLibrary = "java-library"
    const val kotlin = "kotlin"
    const val kotlinKapt = "kotlin-kapt"
    const val daggerHiltPlugin = "dagger.hilt.android.plugin"
}

fun DependencyHandler.classpathL(list: List<String>) {
    list.forEach { dependency ->
        add(ScriptHandler.CLASSPATH_CONFIGURATION, dependency)
    }
}