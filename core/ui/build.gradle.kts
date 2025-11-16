plugins {
    alias(libs.plugins.module.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "pl.jojczak.penmouses.core.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.bundles.compose.core)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.haze)
    implementation(libs.bundles.halilibo.richtext)

    debugImplementation(libs.bundles.compose.debug)
}