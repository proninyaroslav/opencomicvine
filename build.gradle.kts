// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.1" apply false
    id("com.android.library") version "8.8.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.30" apply false
}

// TODO: https://github.com/google/dagger/issues/3068
buildscript {
    configurations.all {
        resolutionStrategy.eachDependency {
            when (requested.name) {
                "javapoet" -> useVersion("1.13.0")
            }
        }
    }
}