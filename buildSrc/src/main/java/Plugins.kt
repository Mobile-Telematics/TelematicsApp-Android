import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

object Plugins {

    private const val gradle = "com.android.tools.build:gradle:${PluginVersion.gradle}"
    private const val kotlin =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginVersion.kotlin}"

    val classpathList = arrayListOf(
        gradle,
        kotlin
    )

    /*---*/

    const val application = "com.android.application"
    const val android = "android"
    const val androidExtensions = "android.extensions"
    const val androidLibrary = "com.android.library"
}

fun DependencyHandler.classpathL(list: List<String>) {
    list.forEach { dependency ->
        add(ScriptHandler.CLASSPATH_CONFIGURATION, dependency)
    }
}