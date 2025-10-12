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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.haze)
    implementation(libs.haze.materials)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}