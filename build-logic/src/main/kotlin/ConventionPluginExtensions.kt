import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.kotlin.dsl.accessors.runtime.extensionOf
import org.gradle.plugin.use.PluginDependency

val Project.libs
    get(): LibrariesForLibs = extensionOf(this, "libs") as LibrariesForLibs

fun PluginManager.aliasConv(notation: Provider<PluginDependency>) {
    apply(notation.get().pluginId)
}

fun PluginManager.aliasConv(notation: ProviderConvertible<PluginDependency>) {
    apply(notation.asProvider().get().pluginId)
}

fun DependencyHandler.implementationConv(provider: Provider<MinimalExternalModuleDependency>) {
    add("implementation", provider.get().group + ":" + provider.get().name + ":" + provider.get().version)
}

fun DependencyHandler.kspConv(provider: Provider<MinimalExternalModuleDependency>) {
    add("ksp", provider.get().group + ":" + provider.get().name + ":" + provider.get().version)
}