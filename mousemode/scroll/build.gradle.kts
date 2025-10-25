plugins {
    alias(libs.plugins.module.android.library)
}

android {
    namespace = "pl.jojczak.penmouses.mousemode.scroll"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":mousemode:base"))

    implementation(files("../../libs/sdk-v1.0.0.jar"))
    implementation(files("../../libs/spenremote-v1.0.1.jar"))

    implementation(libs.androidx.core.ktx)
}