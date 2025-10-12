plugins {
    alias(libs.plugins.module.android.library)
}

android {
    namespace = "pl.jojczak.penmouses.mousemode.mouse"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":mousemode:base"))
    implementation(project(":mousemode:basecursor"))

    implementation(files("../../libs/sdk-v1.0.0.jar"))
    implementation(files("../../libs/spenremote-v1.0.1.jar"))

    implementation(libs.androidx.core.ktx)
}