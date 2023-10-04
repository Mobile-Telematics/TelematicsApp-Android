/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.android.build.api.dsl.LibraryExtension
import com.telematics.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("telematics.android.library")
                apply("telematics.android.hilt")
            }
            extensions.configure<LibraryExtension> {
                buildFeatures {
                    viewBinding = true
                }
            }

            dependencies {
                add("implementation", project(":domain"))
                add("implementation", project(":data"))
                add("implementation", project(":content"))

                add("implementation", libs.findLibrary("javax.inject").get())
                add("implementation", libs.findLibrary("javax.annotation").get())

                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.ktx").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.ktx").get())

                add("implementation", libs.findLibrary("androidx.navigation.fragment.ktx").get())

                add("implementation", libs.findLibrary("androidx.appcompat").get())
                add("implementation", libs.findLibrary("androidx.activity").get())
                add("implementation", libs.findLibrary("androidx.fragment").get())

                add("implementation", libs.findLibrary("androidx.constraintlayout").get())
                add("implementation", libs.findLibrary("androidx.recyclerview").get())
                add("implementation", libs.findLibrary("androidx.swiperefreshlayout").get())

                add("implementation", libs.findLibrary("google.material").get())
            }

        }
    }
}