plugins {
    alias(libs.plugins.module.android.library)
}

android {
    namespace = "pl.jojczak.penmouses.mousemode.scroll"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":mousemode:base"))

    implementation(libs.androidx.core.ktx)
}