// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
buildscript {
    val naverLoginVersion by extra("4.2.6")
    repositories {
        google()
        mavenCentral()
        }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.0")
    }
}



