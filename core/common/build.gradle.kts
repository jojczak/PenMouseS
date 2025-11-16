plugins {
    alias(libs.plugins.module.android.library)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "pl.jojczak.penmouses.core.common"
}

dependencies {
    implementation(project(":core:ui"))

    implementation(libs.androidx.core.ktx)

    implementation(files("../../libs/sdk-v1.0.0.jar"))
    implementation(files("../../libs/spenremote-v1.0.1.jar"))

    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.ksp)
}