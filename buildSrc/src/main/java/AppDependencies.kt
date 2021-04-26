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

    val appLibraries = arrayListOf(
        kotlinStdLib,
        coreKtx,
        appcompat,
        constraintLayout,
        material
    )
}

//fun DependencyHandler.kapt(list: List<String>) {
//    list.forEach { dependency ->
//        add("kapt", dependency)
//    }
//}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}