package pl.jojczak.penmouses.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import pl.jojczak.penmouses.core.ui.R as coreR

@Serializable
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
internal sealed class Screen(
    @param:StringRes val titleResId: Int,
    @param:DrawableRes val iconResId: Int
) {
    @Serializable
    data object Home : Screen(
        titleResId = coreR.string.screen_home,
        iconResId = coreR.drawable.stylus_24px
    )

    @Serializable
    data object Manual : Screen(
        titleResId = coreR.string.screen_manual,
        iconResId = coreR.drawable.menu_book_24px
    )

    @Serializable
    data object Settings : Screen(
        titleResId = coreR.string.screen_settings,
        iconResId = coreR.drawable.settings_24px
    )

    companion object {
        val order = listOf(Home, Manual, Settings)

        fun indexOfRoute(route: String?) = order.indexOf(findFromRoute(route))

        fun findFromRoute(route: String?) = order.find {
            it::class.serializer().descriptor.serialName == route
        } ?: Home
    }
}