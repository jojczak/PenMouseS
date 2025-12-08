import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun Project.setupAndroidModule(isApplication: Boolean) {
    with(pluginManager) {
        if (isApplication) {
            aliasConv(libs.plugins.android.application)
        } else {
            aliasConv(libs.plugins.android.library)
        }
        aliasConv(libs.plugins.kotlin.android)
    }

    extensions.configure<BaseExtension> {
        compileSdkVersion(libs.versions.compileSdk.get().toInt())

        defaultConfig {
            minSdk = libs.versions.minSdk.get().toInt()
            targetSdk = libs.versions.compileSdk.get().toInt()
            versionCode = libs.versions.versionCode.get().toInt()
            versionName = libs.versions.versionName.get()
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}