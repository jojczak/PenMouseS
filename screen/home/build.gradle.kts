plugins {
    alias(libs.plugins.module.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "pl.jojczak.penmouses.screen.home"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))

    implementation(libs.bundles.compose.core)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.haze)
    implementation(libs.bundles.exoplayer)
    implementation(libs.bundles.halilibo.richtext)
    implementation(libs.bundles.review)

    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.ksp)

    debugImplementation(libs.bundles.compose.debug)
}