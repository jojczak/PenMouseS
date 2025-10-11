import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "pl.jojczak.penmouses.mousemode.point"
    compileSdk = 36

    defaultConfig {
        minSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":mousemode:base"))
    implementation(project(":mousemode:basecursor"))

    implementation(files("../../libs/sdk-v1.0.0.jar"))
    implementation(files("../../libs/spenremote-v1.0.1.jar"))

    implementation(libs.androidx.core.ktx)
}