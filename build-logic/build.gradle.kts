import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    `kotlin-dsl`
}

group = "pl.jojczak.penmouses.build-logic"

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)

    gradle.serviceOf<DependenciesAccessors>().classes.asFiles.forEach {
        compileOnly(files(it.absolutePath))
    }
}

gradlePlugin {
    plugins {
        register("AndroidApplicationConventionPlugin") {
            id = "pl.jojczak.penmouses.module.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("AndroidLibraryConventionPlugin") {
            id = "pl.jojczak.penmouses.module.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}